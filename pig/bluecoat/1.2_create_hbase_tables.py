from starbase import Connection

connection = Connection('10.10.30.200', '20550')
connection.tables()

if connection.table_exists('e8'):
  pass
else:
  connection.create_table('e8', 'proxy_raw', 'proxy_time_series', 'features', 'indexes')
table = connection.table('e8')
table.columns()

