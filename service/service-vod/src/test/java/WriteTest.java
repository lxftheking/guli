import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WriteTest {
    @Test
    public void testCellType() throws Exception {

        InputStream is = new FileInputStream("C:\\Users\\saber\\Desktop\\新建 XLS 工作表.xls");
        Workbook workbook = new HSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);




        // 读取商品列表数据
        List<String> list = new ArrayList<>();
        int rowCount = sheet.getPhysicalNumberOfRows();
        System.out.println(rowCount);
        for (int rowNum = 2; rowNum < rowCount; rowNum++) {

            Row rowData = sheet.getRow(rowNum);
            if (rowData != null) {// 行不为空

                Cell cell = rowData.getCell(1);
                list.add(cell.getStringCellValue());
                //System.out.println(cell.getStringCellValue());
            }
        }

        Collections.sort(list,new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                String o1 = s1;
                String o2 = s2;
                for (int i = 0; i < o1.length() && i < o2.length(); i++) {

                    int codePoint1 = o1.charAt(i);
                    int codePoint2 = o2.charAt(i);

                    if (Character.isSupplementaryCodePoint(codePoint1)
                            || Character.isSupplementaryCodePoint(codePoint2)) {
                        i++;
                    }

                    if (codePoint1 != codePoint2) {
                        if (Character.isSupplementaryCodePoint(codePoint1)
                                || Character.isSupplementaryCodePoint(codePoint2)) {
                            return codePoint1 - codePoint2;
                        }

                        String pinyin1 = PinyinHelper.toHanyuPinyinStringArray((char) codePoint1) == null
                                ? null : PinyinHelper.toHanyuPinyinStringArray((char) codePoint1)[0];
                        String pinyin2 = PinyinHelper.toHanyuPinyinStringArray((char) codePoint2) == null
                                ? null : PinyinHelper.toHanyuPinyinStringArray((char) codePoint2)[0];

                        if (pinyin1 != null && pinyin2 != null) { // 两个字符都是汉字
                            if (!pinyin1.equals(pinyin2)) {
                                return pinyin1.compareTo(pinyin2);
                            }
                        } else {
                            return codePoint1 - codePoint2;
                        }
                    }
                }
                return o1.length() - o2.length();
            }
        });


        list.forEach(s -> {
            System.out.println("insert into `edu_school` value(null,\'"+s+"\');");

        });


        is.close();
    }
}
