# Identify and monitor unused indexes in SQL Server

SQL Server indexes are essentially copies of the data that already exist in the table, ordered and filtered in different ways to improve the performance of executed queries. Seeks, scans and lookups operators are used to access SQL Server indexes. <br>

Seeks operators – the Seek operator uses the ability of SQL Server to search indexes to get rows from a clustered or nonclustered indexes, and the seek can be a physical as well as a logical operator. The index seeks only deals with qualified rows and with pages that comprises those qualified rows, and therefore the cost of the seek is less expensive. In simple words, seeks to retrieve just selected rows from the table.<br>
