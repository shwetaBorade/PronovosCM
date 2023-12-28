package com.pronovoscm.utils.database;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**

 DOLE SQLITE breeeeeee!!!
 *i choosed to convert List into one string
 *that is going to be saved in database, and vice versa
 */
public class GreenConverter implements PropertyConverter<List<String>, String> {
    @Override
    public List convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            List lista = new ArrayList();
            if(databaseValue.contains(",")) {
                lista = Arrays.asList(databaseValue.split(","));
            }else {
                lista = Arrays.asList(databaseValue);
            }
            return lista;
        }
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        if(entityProperty==null){
            return null;
        }
        else{
            StringBuilder sb= new StringBuilder();
            for(String link:entityProperty){
                sb.append(link);
                sb.append(",");
            }
            return sb.toString();
        }
    }
}
