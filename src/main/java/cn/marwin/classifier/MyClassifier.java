package cn.marwin.classifier;

import java.io.*;

public class MyClassifier {

    public static final String MODEL_PATH ="/Users/kong/Desktop/微博舆论监控系统/src/main/java/cn/marwin/classifier/weibo-model";
    public static Model model;

    /**
     * 初始化模型
     * @throws IOException 语料库或辅助文档文件不存在
     */
    public static void init() throws IOException {
        Model newModel = importModel(MODEL_PATH);

        if (newModel != null) {
            System.out.println("模型加载成功。");
            model = newModel;
        } else {
            System.out.println("开始训练模型。");
            Train.loadCorpus();
            Train.segmentDocs();
            Train.extractFeatures();
            model = Train.getModel();
            exportModel(model, MODEL_PATH);
        }
        System.out.println("分类器初始化完成。");
    }

    /**
     * 调用模型，对文本进行评估得分
     * @return score为0表示中立，为正表示pos，为负表示neg
     */
    public static double getScore(String text) {
        return model.nb(text);
    }

    /**
     * 保存模型到指定路径
     */
    private static void exportModel(Object model, String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(model);
        } catch (Exception e) {
            System.err.println("保存模型失败：");
            System.err.println(e.getMessage());
        }
    }
    public static String getPath(){
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        java.net.URL url = classLoader.getResource("");
        String ROOT_CLASS_PATH = url.getPath() + "/";
        File rootFile = new File(ROOT_CLASS_PATH);
        String WEB_INFO_DIRECTORY_PATH = rootFile.getParent() + "/";
        File webInfoDir = new File(WEB_INFO_DIRECTORY_PATH);
        // 这里 SERVLET_CONTEXT_PATH 就是WebRoot的路径
        String SERVLET_CONTEXT_PATH = webInfoDir.getParent() + "/";
        System.out.println(SERVLET_CONTEXT_PATH);
        return SERVLET_CONTEXT_PATH;
    }
    /**
     * 从指定路径读取模型
     */
    private static Model importModel(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Object o = ois.readObject();
            return (Model) o;
        } catch (FileNotFoundException e) {
            System.err.println("读取模型失败，因为文件不存在：" + path);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
