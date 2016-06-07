# GreenDao-vs-Realm
A project that aims to compare performances between this two libs, with my custom complex object (did not find a github project with that comparison).

===
Results are (Samsung Galaxy S7 Edge):

```
GreenDaoPerformanceTest: Mass insert of 365 DailyMealApi took : 950 milliseconds
GreenDaoPerformanceTest: Query of one DailyMealApi took : 0 milliseconds
GreenDaoPerformanceTest: Query of all the DailyMealApi (not ordered) took : 4 milliseconds
GreenDaoPerformanceTest: Query of all the DailyMealApi (ordered) took : 4 milliseconds
GreenDaoPerformanceTest: Size of the DailyMeal GreenDao database is : 518 KB (5000 objects total)

RealmPerformanceTest: mass insert of 365 DailyMealRealm took : 330 milliseconds
RealmPerformanceTest: Query of one DailyMealRealm took : 0 milliseconds
RealmPerformanceTest: Query of all the DailyMealRealm (not ordered) took : 1 milliseconds
RealmPerformanceTest: Query of all the DailyMealRealm (ordered) took : 0 milliseconds
RealmPerformanceTest: Size of the DailyMeal Realm database is : 1692 KB (5000 objects total)
```
