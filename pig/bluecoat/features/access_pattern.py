import json

from starbase import Connection


connection = Connection('10.10.30.200', '20550')
connection.tables()

table = connection.table('e8')
table.columns()

table.fetch('all')
table.fetch('2005-05-01T5:00:00.000Z')

hours = json.loads(table.fetch('all')['indexes']['all_hours'])

