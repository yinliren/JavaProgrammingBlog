# Identify and monitor unused indexes in SQL Server

SQL Server indexes are essentially copies of the data that already exist in the table, ordered and filtered in different ways to improve the performance of executed queries. Seeks, scans and lookups operators are used to access SQL Server indexes. <br>

Seeks operators – the Seek operator uses the ability of SQL Server to search indexes to get rows from a clustered or nonclustered indexes, and the seek can be a physical as well as a logical operator. The index seeks only deals with qualified rows and with pages that comprises those qualified rows, and therefore the cost of the seek is less expensive. In simple words, seeks to retrieve just selected rows from the table.<br>
<br>
Scans operators – the Scans operator scans the clustered index, and it is designed to deal with every row in the scanned table regardless of whether the row is qualified or not. A scan operator can be effective for small tables or in a situation where most of the rows are qualified. In simple terms, scans retrieve all the rows from the table.<br>
<br>
Lookups operators – the lookup operator, is used for retrieving the non-key data from the results set retrieved from the nonclustered index. After the rows are retrieved from the nonclustered index, lookups are used for retrieving column information from these rows.<br>
<br>
While proper use of SQL Server indexes can grant improved performance of executed queries and thus the SQL Server in general, setting them up improperly or not setting them where needed, can have the significantly degraded the performance of the executed queries. Moreover, having unnecessary indexes that are not used by queries can be problematic as well.<br>
<br>
SQL Server indexes are an excellent tool for improving the performance of SELECT queries, but at the same time, SQL Server indexes have negative effects on data updates. INSERT, UPDATE, and DELETE operations cause index updating and thus duplicating the data that already exists in the table. As a result, this increases the duration of transactions and the query execution and often can result in locking, blocking, deadlocking and quite frequent execution timeouts. For large databases or tables, the storage space is also affected by redundant indexes. A critical goal, of any SQL Server DBA, is to maintain indexes including creating required indexes but at the same time removing the ones that are not used. <br>
<br>

##Finding unused indexes
SQL Server provides a significant amount of index information via Dynamic Management Views (DMVs). The dm_db_index_usage_stats DMV displays essential information about index usage, and it can be a useful tool in identifying unused SQL Server indexes. When an index is used for the first time, a new row gets created in the dm_db_index_usage_stats DMV and subsequently updated every time an index is used. However, as with every DMV, the data present in dm_db_index_usage_stats contain only the data since the last SQL Server service restart (SQL Server service restart resets the data in the DMV). Therefore, it is critical that there is a sufficient time since the last SQL Server restart that allows correctly determining which indexes are good candidates to be dropped. <br>

A simple query that can be used to get the list of unused indexes in SQL Server (updated indexes not used in any seeks, scan or lookup operations) is as follows:<br>
```
SELECT
    objects.name AS Table_name,
    indexes.name AS Index_name,
    dm_db_index_usage_stats.user_seeks,
    dm_db_index_usage_stats.user_scans,
    dm_db_index_usage_stats.user_updates
FROM
    sys.dm_db_index_usage_stats
    INNER JOIN sys.objects ON dm_db_index_usage_stats.OBJECT_ID = objects.OBJECT_ID
    INNER JOIN sys.indexes ON indexes.index_id = dm_db_index_usage_stats.index_id AND dm_db_index_usage_stats.OBJECT_ID = indexes.OBJECT_ID
WHERE
    AND
    dm_db_index_usage_stats.user_lookups = 0
    AND
    dm_db_index_usage_stats.user_seeks = 0
    AND
    dm_db_index_usage_stats.user_scans = 0
ORDER BY
    dm_db_index_usage_stats.user_updates DESC
```
The above query returns all unused indexes of all types. This query can frequently be found on the internet but isn’t an ideal/complete option. By using such a query to find and clean unused indexes may lead to unexpected behavior because this query does not take into account primary key and unique key constraints when collecting the unused index data. Both, primary and unique key constraints indexes could be “unused,” but deleting those indexes could be problematic. To prevent that scenario, the query above must be refined by adding two lines of code after the WHERE to exclude the primary and unique keys from being listed as unused and potentially deleted. <br>
```
SELECT
    objects.name AS Table_name,
    indexes.name AS Index_name,
    dm_db_index_usage_stats.user_seeks,
    dm_db_index_usage_stats.user_scans,
    dm_db_index_usage_stats.user_updates
FROM
    sys.dm_db_index_usage_stats
    INNER JOIN sys.objects ON dm_db_index_usage_stats.OBJECT_ID = objects.OBJECT_ID
    INNER JOIN sys.indexes ON indexes.index_id = dm_db_index_usage_stats.index_id AND dm_db_index_usage_stats.OBJECT_ID = indexes.OBJECT_ID
WHERE
    indexes.is_primary_key = 0 -- This condition excludes primary key constarint
    AND
    indexes. is_unique = 0 -- This condition excludes unique key constarint
    AND
    dm_db_index_usage_stats. user_lookups = 0
    AND
    dm_db_index_usage_stats.user_seeks = 0
    AND
    dm_db_index_usage_stats.user_scans = 0
ORDER BY
    dm_db_index_usage_stats.user_updates DESC
```

The above query lists all unused queries that are not primary and unique keys, but it also lists all unused indexes that SQL Server has not worked with. The user_updates column in the dm_db_index_usage_stats DMV is counting where the index was updated as the application has carried some changes to data, so the index was updated. To do that the dm_db_index_usage_stats.user_updates <> 0 conditions should be added to the previous script. <br>
```
SELECT
    objects.name AS Table_name,
    indexes.name AS Index_name,
    dm_db_index_usage_stats.user_seeks,
    dm_db_index_usage_stats.user_scans,
    dm_db_index_usage_stats.user_updates
FROM
    sys.dm_db_index_usage_stats
    INNER JOIN sys.objects ON dm_db_index_usage_stats.OBJECT_ID = objects.OBJECT_ID
    INNER JOIN sys.indexes ON indexes.index_id = dm_db_index_usage_stats.index_id AND dm_db_index_usage_stats.OBJECT_ID = indexes.OBJECT_ID
WHERE
    indexes.is_primary_key = 0 --This line excludes primary key constarint
    AND
    indexes. is_unique = 0 --This line excludes unique key constarint
    AND 
    dm_db_index_usage_stats.user_updates <> 0 -- This line excludes indexes SQL Server hasn’t done any work with
    AND
    dm_db_index_usage_stats. user_lookups = 0
    AND
    dm_db_index_usage_stats.user_seeks = 0
    AND
    dm_db_index_usage_stats.user_scans = 0
ORDER BY
    dm_db_index_usage_stats.user_updates DESC
```
So now that unused SQL Server indexes are identified and listed, it can be determined which indexes can be dropped safely, but again that has to be done very carefully.<br>
<br>
## Which unused indexes should not be removed?
#### Unique constraints<br>
<br>
An example of additional reasons for caution is that the index might be listed as unused, but it might be enforcing a unique constraint, and it is likely that the query optimizer might need this index. The query optimizer might use a guarantee of uniqueness in determining what logical transformations and physical operations should be used for obtaining accurate results. The query optimizer takes into account a uniqueness guarantee to perform certain operations, but this is not echoed in index usage statistics without accessing the index physically in the final execution plan. Having that in mind any removal of unique index or constraint must be taken with the utmost precaution. <br>

#### Use statistics <br>
<br>
Another thing to be careful with is the possibility that the query optimizer use statistic that is associated to that index even in situations where the final execution plan does not use any access to that index. The cardinality estimates, loading of candidates for statistics and finally creating a completed query execution plan are entirely independent actions. <br>
<br>
<br>
Finally, removing the index could remove the accompanying index statistics as well. That can impact query execution plan quality when the statement is recompiled. it is because the query execution plan might use the index statistics, even when the index is not physically present in the final execution plan, for calculating cardinality estimation, which is something that the final execution plan significantly relies on. <br>
<br>
Those are just some of the potential problems that could be encountered when dropping the index, and therefore such an action has to be planned by performing the adequate testing and with the plan for recovery if something goes wrong. On top of that, having some unused SQL Server indexes do not necessarily indicate a problem, but if the number of unused indexes grows over the time at some more or less constant rate or when there is a sudden growth, this is something that must be inspected and, in most cases, tested. <br>
<br>

#### Dropping the indexes
The following script creates a drop script for all unused indexes. It is based on the previous script that is safer, but it is provided as a guide, and any deletion of indexes is on users own discretion. The script purpose id to help identify indexes that are candidates to remove, so don’t decide on that in a bubble: <br>
```
SELECT 'DROP INDEX '+OBJECT_NAME(dm_db_index_usage_stats.object_id)+'.'+indexes.name AS Drop_Index, user_seeks, user_scans, user_lookups, user_updates
 
FROM
    sys.dm_db_index_usage_stats
    INNER JOIN sys.objects ON dm_db_index_usage_stats.OBJECT_ID = objects.OBJECT_ID
    INNER JOIN sys.indexes ON indexes.index_id = dm_db_index_usage_stats.index_id AND dm_db_index_usage_stats.OBJECT_ID = indexes.OBJECT_ID
WHERE
    indexes.is_primary_key = 0 --This line excludes primary key constarint
    AND
    indexes. is_unique = 0 --This line excludes unique key constarint
    AND 
    dm_db_index_usage_stats.user_updates <> 0 -- This line excludes indexes SQL Server hasn’t done any work with
    AND
    dm_db_index_usage_stats. user_lookups = 0
    AND
    dm_db_index_usage_stats.user_seeks = 0
    AND
    dm_db_index_usage_stats.user_scans = 0
ORDER BY
    dm_db_index_usage_stats.user_updates DESC
```
