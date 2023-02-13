package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
//        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
//        mealsTo.forEach(System.out::println);
//
//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime,
                                                            LocalTime endTime, int caloriesPerDay) {

        Set<LocalDateTime> dateSet = new HashSet<>();
        for(UserMeal meal : meals) {
            dateSet.add(meal.getDateTime());
        }

        Map<LocalTime, Integer> caloriesSumByDay = new HashMap<>();
        for (LocalDateTime dateTime : dateSet) {
            int sumCalories = 0;
            for (UserMeal meal : meals) {
                if(dateTime.equals(meal.getDateTime())){
                    sumCalories += meal.getCalories();
                }
            }
            caloriesSumByDay.put(dateTime.toLocalTime(), sumCalories);
        }


        List<UserMealWithExcess> userMealWithExcessList = new ArrayList<>();
        for(UserMeal meal : meals) {
            if(TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                userMealWithExcessList.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(),
                        meal.getCalories(), caloriesSumByDay.get(meal.getDateTime().toLocalTime()) > caloriesPerDay));
            }
        }

        return userMealWithExcessList;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime,
                                                             LocalTime endTime, int caloriesPerDay) {

        Map<LocalTime, Integer> caloriesSumByDay = meals.stream()
                .collect(Collectors.groupingBy(x -> x.getDateTime().toLocalTime(),
                        Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream().filter(x -> TimeUtil.isBetweenHalfOpen(x.getDateTime().toLocalTime(), startTime, endTime))
                .map(x -> new UserMealWithExcess(x.getDateTime(), x.getDescription(), x.getCalories(),
                        caloriesSumByDay.get(x.getDateTime().toLocalTime()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
