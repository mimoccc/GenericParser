package sk.solver.common.genericparser;

/*
* Base interface for reasons that autogeneration should be extended
* or need another functionality
* */
public interface IGPRow {
    int cols();

    String col(int idx);

    <T> T asType(Class<T> cls);
}
