DROP ALIAS IF EXISTS SPLIT_PART;
CREATE ALIAS SPLIT_PART AS '
import java.text.*;
@CODE
String SPLIT_PART(String originalString, String delimiter, int index) throws Exception {
    return originalString.split(delimiter)[index-1];
}
';
