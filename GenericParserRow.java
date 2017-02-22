package sk.solver.common.genericparser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
* Generic parser row, containing cols
* */
public class GenericParserRow extends ArrayList<String> implements IGPRow {
    private String file_name;
    private String file_path;

    public GenericParserRow() {
        super();
    }

    public GenericParserRow(File f, String text, String delimiter) throws IOException {
        super();

        this.file_name = f.getName();
        this.file_path = f.getAbsolutePath();

        if (text != null) {
            addAll(Arrays.asList(text.split("\\" + (delimiter == null ? "|" : delimiter), -1)));
        }
    }

    @Override
    public int cols() {
        return size();
    }

    @Override
    public String col(int idx) {
        return (size() > idx) ? get(idx) : "";
    }

    public <T> T asType(Class<T> cls) {
        return GenericParser.getMapper().convertValue(toMap(cls), cls);
    }

    private HashMap<String, String> toMap(Class<?> cls) {
        HashMap<String, String> map = new HashMap<String, String>();

        List<Field> fields = getPrivateFields(cls);

        if (fields != null) {
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);

                if (f.isAnnotationPresent(GPFileName.class)) {
                    map.put(f.getName(), file_name);
                } else if (f.isAnnotationPresent(GPFilePath.class)) {
                    map.put(f.getName(), file_path);
                } else if (f.isAnnotationPresent(GPProperty.class)) {
                    int idx = f.getAnnotation(GPProperty.class).index();

                    map.put(f.getName(), col(idx));
                }
            }
        }

        return map;
    }

    public List<Field> getPrivateFields(Class<?> theClass) {
        List<Field> privateFields;

        if(GenericParser.containsFiledsMap(theClass)) {
            privateFields = GenericParser.getFieldsMap(theClass);
        } else {
            privateFields = new ArrayList<Field>();

            Field[] fields = theClass.getDeclaredFields();

            for (Field field : fields) {
                privateFields.add(field);
            }

            GenericParser.putFieldsMap(theClass, privateFields);
        }

        return privateFields;
    }
}
