package com.nami.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nami.springbootinit.common.ErrorCode;
import com.nami.springbootinit.config.BloomFilterUtil;
import com.nami.springbootinit.constant.CommonConstant;
import com.nami.springbootinit.exception.BusinessException;
import com.nami.springbootinit.mapper.UserMapper;
import com.nami.springbootinit.model.dto.user.UserQueryRequest;
import com.nami.springbootinit.model.dto.user.UserUpdateMyRequest;
import com.nami.springbootinit.model.entity.Sign;
import com.nami.springbootinit.model.entity.User;
import com.nami.springbootinit.model.enums.UserRoleEnum;
import com.nami.springbootinit.model.vo.LoginUserVO;
import com.nami.springbootinit.model.vo.UserVO;
import com.nami.springbootinit.service.ChartService;
import com.nami.springbootinit.service.SignService;
import com.nami.springbootinit.service.UserService;
import com.nami.springbootinit.utils.SqlUtils;

import static com.nami.springbootinit.constant.UserConstant.USER_LOGIN_STATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现
 
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "nami";

    @Resource
    private ChartService chartService;

    @Resource
    private SignService signService;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);

        long chartCount = chartService.getChartListByUserId(user.getId()).stream().count();
        loginUserVO.setGeneratingCount(chartCount);

        Sign sign = signService.getByUserId(user.getId());
        if (sign == null) {
            loginUserVO.setScore(0);
            loginUserVO.setSignInStatus(0);
        } else {
            loginUserVO.setScore(sign.getScore());
            loginUserVO.setSignInStatus(sign.getSignInStatus());
            loginUserVO.setSignInTime(sign.getSignInTime());
        }
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Long updateUserMy(UserUpdateMyRequest  userUpdateMyRequest, Long userId) {
        String userAccount = userUpdateMyRequest.getUserAccount();
        String userPassword = userUpdateMyRequest.getUserPassword();
        String checkPassword = userUpdateMyRequest.getCheckPassword();

        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 更新数据
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", userId)
                    .set("userAccount", userAccount)
                    .set("userPassword", encryptPassword)
                    .set("userName", userUpdateMyRequest.getUserName())
                    .set("userAvatar", userUpdateMyRequest.getUserAvatar());
            boolean updateResult = this.update(updateWrapper);
            if (!updateResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败，数据库错误");
            }
            return userId;
        }
    }

    // 预期插入数量
    static long expectedInsertions = 200L;
    // 误判率
    static double falseProbability = 0.01;

    @Resource
    private BloomFilterUtil bloomFilterUtil;

    private RBloomFilter<String> bloomFilter = null;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserMapper userMapper;

    // 非法请求所返回的JSON
    static String illegalJson = "[\"com.nami.springbootinit.model.entity.User\",{\"id\":null,\"userAccount\":\"null\",\"userName\":null}]";


    @PostConstruct // 项目启动的时候执行该方法，也可以理解为在spring容器初始化的时候执行该方法
    public void init() {
        // 启动项目时初始化bloomFilter
        List<User> userList = this.list();
        bloomFilter = bloomFilterUtil.create("idWhiteList", expectedInsertions, falseProbability);
        for (User user : userList) {
            bloomFilter.add(user.getUserAccount());
        }
    }

    @Cacheable(cacheNames = "user", key = "#userAccount", unless = "#result==null")
    public User findById(String userAccount) {
        // bloomFilter中不存在该key,为非法访问
        if (!bloomFilter.contains(userAccount)) {
            System.out.println("所要查询的数据既不在缓存中，也不在数据库中，为非法key");
            /**
             * 设置unless = "#result==null"并在非法访问的时候返回null的目的是不将该次查询返回的null使用
             * RedissonConfig-->RedisCacheManager-->RedisCacheConfiguration-->entryTtl设置的过期时间存入缓存。
             *
             * 因为那段时间太长了，在那段时间内可能该非法key又添加到bloomFilter，比如之前不存在id为1234567的用户，
             * 在那段时间可能刚好id为1234567的用户完成注册，使该key成为合法key。
             *
             * 所以我们需要在缓存中添加一个可容忍的短期过期的null或者是其它自定义的值,使得短时间内直接读取缓存中的该值。
             *
             * 因为Spring Cache本身无法缓存null，因此选择设置为一个其中所有值均为null的JSON，
             */
            redissonClient.getBucket("user::" + userAccount, new StringCodec()).set(illegalJson, new Random().nextInt(200) + 300, TimeUnit.SECONDS);
            return null;
        }
        // 不是非法访问，可以访问数据库
        System.out.println("数据库中得到数据*****");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        return this.baseMapper.selectOne(queryWrapper);
    }

    // 先执行方法体中的代码，成功执行之后删除缓存
    @CacheEvict(cacheNames = "user", key = "#userAccount")
    public boolean delete(Long id) {
        // 删除数据库中具有的数据,就算此key从此之后不再出现，也不能从布隆过滤器删除
        return userMapper.deleteById(id) == 1;
    }

    // 如果缓存中先前存在，则更新缓存;如果不存在，则将方法的返回值存入缓存
    @CachePut(cacheNames = "user", key = "#user.id")
    public User update(User user) {
        userMapper.updateById(user);
        // 新生成key的加入布隆过滤器，此key从此合法,因为该更新方法并不更新id,所以也不会产生新的合法的key
        bloomFilter.add(user.getUserAccount());
        return user;
    }

    @CachePut(cacheNames = "user", key = "#user.userAccount")
    public User insert(User user) {
        userMapper.insert(user);
        // 新生成key的加入布隆过滤器，此key从此合法
        bloomFilter.add(user.getUserAccount());
        return user;
    }
}
