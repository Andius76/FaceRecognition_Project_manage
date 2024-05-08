package opencv.sample;


import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.Arrays;
import java.util.Base64;



/**
 * @author mijiupro
 */

public class FaceDetection {

    //人脸检测器
    private static final CascadeClassifier FACE_DETECTOR;

    //人脸模型xml文件路径
    private static final String FACE_MODEL_PATH = "src/main/java/opencv/model/haarcascade_frontalface_alt.xml";
    //测试图片文件夹路径
    private static final String TRAINING_DATA_DIR = "src/main/java/opencv/sample";
    //检测并处理后保存图片路径
    private static final String DETECTION_RESULT ="src/main/java/opencv/result/result.jpg";

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);//加载库文件
        FACE_DETECTOR = new CascadeClassifier(FACE_MODEL_PATH);//创建级联分类器加载人脸模型xml文件
    }

    /**
     * 测试模型
     */
    public static void testModel() {
        // 加载样本图片
        Mat image = Imgcodecs.imread(TRAINING_DATA_DIR+"/1.jpg");
        // 检测人脸
        MatOfRect faceDetections = new MatOfRect();
        FACE_DETECTOR.detectMultiScale(image, faceDetections);

        int numFaces = faceDetections.toArray().length;
        System.out.println("人脸数量: " + numFaces);
        // 绘制标人脸识框并打印人脸位置坐标
        Arrays.stream(faceDetections.toArray()).forEach(
                (rect) -> {
                    Imgproc.rectangle(image, new Point(rect.x, rect.y),
                            new Point(rect.x + rect.width, rect.y + rect.height),
                            new Scalar(0, 255, 0));
                    System.out.println("坐标： (" + rect.x + ", " + rect.y + ")");
                });

        // 保存处理后的图片
        Imgcodecs.imwrite(DETECTION_RESULT, image);
    }

    public static void main(String[] args) {
        testModel();
    }
}