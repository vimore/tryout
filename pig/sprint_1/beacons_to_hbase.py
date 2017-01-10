import jaydebeapi

conn = jaydebeapi.connect('com.salesforce.phoenix.jdbc.PhoenixDriver',
                          ['jdbc:phoenix:hiveapp1:2181'],
                          '/Users/rjurney/Downloads/phoenix-2.1.2/phoenix-2.1.2-client.jar')