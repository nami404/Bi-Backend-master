package com.nami.springbootinit.otherttest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nami404
 * * @date 2025/3/6 13:00
 */
@SpringBootTest
public class StreamTest {

    @Test
    public void testOne() {
        List<Integer> list = Arrays.asList(4, 6, 2, 6, 67, 4, 5, 2, 12);
        List<Double> collect = list.stream()
                .distinct()
                .map(Math::sqrt)
                .collect(Collectors.toList());
        System.out.println("处理后的结果" + collect);
    }

    @Test
    public void testTwo() {
        List<Person> people = Arrays.asList(
                new Person("Alice", 25),
                new Person("Bob", 30),
                new Person("Alice", 35)
        );

        // 使用合并函数处理重复键
//        Map<String, Integer> ageByName = people.stream()
//                .collect(Collectors.toMap(
//                        Person::getName,
//                        Person::getAge,
//                        (existing, replacement) -> existing // 保留已存在的值
//                ));

        Map<String, List<Integer>> ageByName = people.stream()
                .collect(Collectors.groupingBy(Person::getName,
                        Collectors.mapping(Person::getAge, Collectors.toList())));

        ageByName.forEach((name, ages) -> {
            System.out.print(name + ":");
            ages.forEach(age -> System.out.print(age + " "));
            System.out.println();
        });
    }
}
