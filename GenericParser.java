package sk.solver.common.genericparser;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sk.solver.common.FileManager.FileManager;

/*
* Generic file parser to parse text or csv file to object directly
* Please use it as with example object : SheetRecord (see annotations to get index in file)
*
* todo:
* - reversa packaging to file (not primar task
* - property checking * default values)
* */
public abstract class GenericParser<T> extends ArrayList<IGPRow> {
    public static final String LINE_DELIMITER = "\n";

    private static ObjectMapper mapper;
    private static HashMap<Class<?>, List<Field>> fieldsMap;

    private int max_col_idx;

    static {
        fieldsMap = new HashMap<Class<?>, List<Field>>();

        mapper = new ObjectMapper();

        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, true);
    }

    public GenericParser(File f, String row_delimiter, String col_delimiter) throws IOException {
        super();

        String text = FileManager.readFile(f);

        if (text != null) {
            parseRows(f, text, row_delimiter, col_delimiter);
        }
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    private void parseRows(File f, String text, String row_delimiter, String col_delimiter) {
        String[] rows = text.split(row_delimiter);

        if (rows != null) {
            for (String s : rows) {
                IGPRow cr = parseRow(f, s, col_delimiter);

                if (cr != null) {
                    max_col_idx = (max_col_idx > cr.cols()) ? max_col_idx : cr.cols();

                    add(cr);
                }
            }
        }
    }

    public int rows() {
        return size();
    }

    public int cols() {
        return max_col_idx;
    }

    public ArrayList<T> toList(Class<T> cls) {
        ArrayList<T> ret = new ArrayList<T>();

        for (int i = 0; i < size(); i++) {
            T item = row(i).asType(cls);

            if (item.getClass().isAnnotationPresent(GPPostProcess.class)) {
                item = postProcess(this, item);
            }

            if (item != null) {
                ret.add(item);
            }
        }

        return ret;
    }

    public IGPRow row(int idx) {
        return (size() > idx) ? get(idx) : new GenericParserRow();
    }

    public abstract IGPRow parseRow(File f, String text, String col_delimiter);

    public abstract T postProcess(GenericParser icsvRows, T data);

    public static boolean containsFiledsMap(Class<?> theClass) {
        return fieldsMap.containsKey(theClass);
    }

    public static List<Field> getFieldsMap(Class<?> theClass) {
        return fieldsMap.get(theClass);
    }

    public static void putFieldsMap(Class<?> theClass, List<Field> fieldList) {
        fieldsMap.put(theClass, fieldList);
    }
}
