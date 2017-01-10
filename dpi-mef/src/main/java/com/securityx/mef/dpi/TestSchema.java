package com.securityx.mef.dpi;

//import example.avro.User;

/**
 * Created with IntelliJ IDEA.
 * User: jeyasankar
 * Date: 11/7/13
 * Time: 8:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSchema {

    public static  void main (String[] args)  throws Exception {

//        User user1 = new User();
//        user1.setName("Alyssa");
//        user1.setFavoriteNumber(256);
//// Leave favorite color null
//
//// Alternate constructor
//        User user2 = new User("Ben", 7, "red");
//
//// Construct via builder
//        User user3 = User.newBuilder()
//                .setName("Charlie")
//                .setFavoriteColor("blue")
//                //.setFavoriteNumber(null)
//                .build();
//
//        // Serialize user1 and user2 to disk
//        File file = new File("/Users/jeyasankar/mytemp/users2.avro");
//        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
//        DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
//        dataFileWriter.create(user1.getSchema(), file ) ; //new File("/Users/jeyasankar/mytemp/users2.avro"));
//        dataFileWriter.append(user1);
//        dataFileWriter.append(user2);
//        dataFileWriter.append(user3);
//        dataFileWriter.close();
//
//
//        // Deserialize Users from disk
//        DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
//        DataFileReader<User> dataFileReader = new DataFileReader<User>(file, userDatumReader);
//        User user = null;
//        while (dataFileReader.hasNext()) {
//// Reuse user object by passing it to next(). This saves us from
//// allocating and garbage collecting many objects for files with
//// many items.
//            user = dataFileReader.next(user);
//            System.out.println(user);
//        }




    }
}
