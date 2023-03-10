package cn.marwin.classifier;

import cn.marwin.util.FileUtil;
import cn.marwin.util.SegmentUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.swing.tree.RowMapper;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

class TrainTest {

    /**
     * 测试模型训练过程
     */
    @Test
    void train() throws IOException {
        MyClassifier.init();
        MyClassifier.model.printFeatures();
    }

    /**
     * 测试模型准确率
     */
    void test() throws IOException {
        // 测试文档路径
        String posPath = "/Users/kong/Desktop/微博舆论监控系统/src/main/java/cn/marwin/classifier/pos.txt";
        String negPath = "/Users/kong/Desktop/微博舆论监控系统/src/main/java/cn/marwin/classifier/neg.txt";
        List<String> posComments = FileUtil.fileToList(posPath);
        List<String> negComments = FileUtil.fileToList(negPath);

        MyClassifier.init();
        int count = 0;
        for (String comment : posComments) {
            double p = MyClassifier.getScore(comment);
            if (p > 0) {
                count++;
            } else {
                System.out.println(comment);
                System.out.println("情感分析结果为：" + p);
            }
        }

        for (String comment : negComments) {
            double p = MyClassifier.getScore(comment);
            if (p < 0) {
                count++;
            } else {
                System.out.println(comment);
                System.out.println("情感分析结果为：" + p);
            }
        }

        double result = 1.0 * count / (posComments.size() + negComments.size());
        System.out.println("模型在测试集上的准确率为：" + result);
    }

    /**
     * 测试单个文本判断效果
     */
    void testOne() throws IOException {
        MyClassifier.init();
        String text = "";
        System.out.println(SegmentUtil.segment(text));
        System.out.println(MyClassifier.getScore(text));
    }

    /**
     * 测试对否定词的分词情况
     */
    void segement() throws IOException {
        String text = "警惕不是恐慌，不要被影响正常生活。大家做好防护工作是没问题的";
        System.out.println(SegmentUtil.segment(text));
    }

    /**
     * 测试对HanLP动态添加用户自定义词典
     */
    void customDictionary() {
        String text = "营销号专买热搜，杠精键盘侠都来了。就这样还开学，不够人数，不给检测";
        System.out.println(HanLP.segment(text));

        CustomDictionary.add("杠精");
        CustomDictionary.add("键盘侠");
        CustomDictionary.add("热搜");
        CustomDictionary.add("营销号");
        System.out.println(HanLP.segment(text));
        System.out.println(SegmentUtil.segment(text));
    }


}
