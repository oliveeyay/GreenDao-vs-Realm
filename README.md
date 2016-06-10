# GreenDao-vs-Realm
A project that aims to compare performances between this two libs, with my custom complex object (did not find a github project with that comparison).

===
Results are (all the logic is done in androidTest):

- Samsung Galaxy S7 Edge (6.0.1):
```
GreenDaoPerformanceTest: Insert of 1 DailyMealApi took : 179 milliseconds
GreenDaoPerformanceTest: Mass insert of 365 DailyMealApi took : 950 milliseconds
GreenDaoPerformanceTest: Query of one DailyMealApi took : 0 milliseconds
GreenDaoPerformanceTest: Query of all the DailyMealApi (not ordered) took : 4 milliseconds
GreenDaoPerformanceTest: Query of all the DailyMealApi (ordered) took : 4 milliseconds
GreenDaoPerformanceTest: Size of the DailyMeal GreenDao database is : 185 KB

RealmPerformanceTest: Insert of 1 DailyMealRealm took : 44 milliseconds
RealmPerformanceTest: Mass insert of 365 DailyMealRealm took : 330 milliseconds
RealmPerformanceTest: Query of one DailyMealRealm took : 0 milliseconds
RealmPerformanceTest: Query of all the DailyMealRealm (not ordered) took : 1 milliseconds
RealmPerformanceTest: Query of all the DailyMealRealm (ordered) took : 0 milliseconds
RealmPerformanceTest: Size of the DailyMeal Realm database is : 588 KB
```

- HTC One (4.3):
```
GreenDaoPerformanceTest: Insert of 1 DailyMealApi took : 1035 milliseconds
GreenDaoPerformanceTest: Mass insert of 365 DailyMealApi took : 2220 milliseconds
GreenDaoPerformanceTest: Query of one DailyMealApi took : 1 milliseconds
GreenDaoPerformanceTest: Query of all the DailyMealApi (not ordered) took : 6 milliseconds
GreenDaoPerformanceTest: Query of all the DailyMealApi (ordered) took : 6 milliseconds
GreenDaoPerformanceTest: Size of the DailyMeal GreenDao database is : 185 KB

RealmPerformanceTest: Insert of 1 DailyMealRealm took : 42 milliseconds
RealmPerformanceTest: Mass insert of 365 DailyMealRealm took : 1101 milliseconds
RealmPerformanceTest: Query of one DailyMealRealm took : 1 milliseconds
RealmPerformanceTest: Query of all the DailyMealRealm (not ordered) took : 3 milliseconds
RealmPerformanceTest: Query of all the DailyMealRealm (ordered) took : 0 milliseconds
RealmPerformanceTest: Size of the DailyMeal Realm database is : 588 KB
```

- Samsung Galaxy S3 (4.4.2):
```
GreenDaoPerformanceTest: Insert of 1 DailyMealApi took : 549 milliseconds
GreenDaoPerformanceTest: Mass insert of 365 DailyMealApi took : 2821 milliseconds
GreenDaoPerformanceTest: Query of one DailyMealApi took : 1 milliseconds
GreenDaoPerformanceTest: Query of all the DailyMealApi (not ordered) took : 9 milliseconds
GreenDaoPerformanceTest: Query of all the DailyMealApi (ordered) took : 9 milliseconds
GreenDaoPerformanceTest: Size of the DailyMeal GreenDao database is : 185 KB

RealmPerformanceTest: Insert of 1 DailyMealRealm took : 30 milliseconds
RealmPerformanceTest: Mass insert of 365 DailyMealRealm took : 1017 milliseconds
RealmPerformanceTest: Query of one DailyMealRealm took : 0 milliseconds
RealmPerformanceTest: Query of all the DailyMealRealm (not ordered) took : 3 milliseconds
RealmPerformanceTest: Query of all the DailyMealRealm (ordered) took : 0 milliseconds
RealmPerformanceTest: Size of the DailyMeal Realm database is : 588 KB
```