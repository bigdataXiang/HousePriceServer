
        package com.reprocess.grid_100.interpolation;

        import com.mongodb.BasicDBObject;
        import com.mongodb.Cursor;
        import com.mongodb.DBCollection;
        import com.mongodb.DBCursor;
        import com.svail.db.db;
        import net.sf.json.JSONObject;
        import utils.FileTool;

        import java.util.ArrayList;
        import java.util.List;

        /**
 * Created by ZhouXiang on 2016/9/23.
 */
        public class InformationFusion {

            public static void main(String[] args){

                int year=2015;
                int month=10;
                String source="woaiwojia";
                int code=1092942;

                JSONObject condition=new JSONObject();
                condition.put("code",code);
                condition.put("year",year);
                condition.put("month",month);
                condition.put("source",source);
                condition.put("export_collName","GridData_Resold_100");

                callFromMongo(condition);

    }
    public static void callFromMongo(JSONObject condition){

        String collName=condition.getString("export_collName");
        DBCollection coll = db.getDB().getCollection(collName);

        int code=condition.getInt("code");
        //int year=condition.getInt("year");
       // int month=condition.getInt("month");
        String source=condition.getString("source");

        BasicDBObject document = new BasicDBObject();
        document.put("code",code);
        //document.put("year",year);
        //document.put("month",month);
        document.put("source",source);

        String poi;
        System.out.println(document);
        DBCursor cursor = coll.find(document);
        if(cursor.hasNext()) {
            while (cursor.hasNext()) {
                poi=cursor.next().toString();
                System.out.println(poi);
               // FileTool.Dump(poi.toString(),"D:\\test\\infofusion\\grid\\"+source+".txt","utf-8");
            }
        }

    }
}
