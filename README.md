# GenericParser
Java parser to be embeeded if You need to parse specific text file to an object list.
Using annotations & java reflection, with cached classes.

Example of use:

(data spec:)

@GPPostProcess // use postprocess routine for this class
public class SomeRecord implements Comparable<SomeRecord> {
    @GPFileName // insert filename to property
    private String parsed_file_filename;

    @GPProperty(index = 0) // parse first column in a row in file
    private int page_id;

    @GPProperty(index = 3) // parse 3th field in a row from file
    private String page_package;

    @GPProperty(index = 7)
    private String page_number;

    @GPProperty(index = 8)
    private String label;
    
    // getters & setters

    @Override
    public boolean equals(Object o) {
        SomeRecord t = (o instanceof SomeRecord) ? (SomeRecord) o : null;

        return (t == null) ? false : ((Integer) page_id).equalsIgnoreCase(((SomeRecord)t).getPageId());
    }

    /*
    * To be visual human readable
    * */
    @Override
    public String toString() {
        return String.format("%s|%s", parsed_file_filename, page_id);
    }
}

(parser usage:)

public List<SomeRecord> parseFileItems(File file) {
        try {
            GenericParser parser = new GenericParser<SomeRecord>(file, GenericParser.LINE_DELIMITER, DELIMITER) {
                @Override // preprocessing routine
                public IGPRow parseRow(File f, String text, String col_delimiter) {
                    try {
                        GenericParserRow row = new GenericParserRow(f, text, col_delimiter);

                        if(row.col(0) == null) {
                            row = null; // remove all rows which does not have record data at first column
                        }

                        return row;
                    } catch (IOException e) {
                        throw (new RuntimeException(e));
                    }
                }

                @Override
                public SheetRecord postProcess(GenericParser icsvRows, SomeRecord data) {
                    // post process data

                    return data;
                }
            };

            return parser.toList(SomeRecord.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

