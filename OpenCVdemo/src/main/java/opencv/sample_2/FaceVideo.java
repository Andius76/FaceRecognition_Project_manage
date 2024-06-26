package opencv.sample_2;


import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import java.util.Arrays;

/**
 * Opencv 图片人脸识别、实时摄像头人脸识别、视频文件人脸识别
 */
public class FaceVideo {

    // 初始化人脸探测器
    static CascadeClassifier faceDetector;

    static int i = 0;

    static {
        // 判断系统
        String os = System.getProperty("os.name");

        // 加载动态库
        if (os != null && os.toLowerCase().startsWith("windows")) {
            // Windows操作系统
            // todo windows 系统部署加载 .dll 文件 - 路径跟据自己存放位置更改
            System.load("C:\\Users\\ANDIUS\\Desktop\\AndiusIPS(Information Processing System).24.0.0\\opencv-4.8.0\\opencv\\build\\java\\x64\\opencv_java480.dll");
//            ClassLoader.getSystemResource("dlls/opencv_java470.dll");
        } else if (os != null && os.toLowerCase().startsWith("linux")) {
            // Linux操作系统
            // todo Linux 服务器部署加载 .so 文件 - 路径跟据自己存放位置更改
            System.load("/opt/face/libopencv_java440.so");
        }

        // 引入 特征分类器配置 文件：haarcascade_frontalface_alt.xml 文件路径
        String property = "src/main/java/opencv/model/haarcascade_frontalface_alt.xml";
        System.out.println(property);
        faceDetector = new CascadeClassifier(property);
    }

    private static final String PATH_PREFIX = "C:\\Users\\ANDIUS\\Desktop\\AndiusIPS(Information Processing System).24.0.0\\OpenCVdemo\\src\\main\\resources";

    public static void main(String[] args) {
        // 1- 从摄像头实时人脸识别，识别成功保存图片到本地
        //getVideoFromCamera();

        // 2- 从本地视频文件中识别人脸
        //getVideoFromFile();

        // 3- 本地图片人脸识别，识别成功并保存人脸图片到本地
        face("5-1.jpg");
        writeVideo();
        // 4- 比对本地2张图的人脸相似度 （越接近1越相似）
        double compareHist = compare_image(PATH_PREFIX + "/5-1.jpg", PATH_PREFIX + "/6-1.jpg");
        System.out.println(compareHist);
        if (compareHist > 0.72) {

            System.out.println("人脸匹配");
        } else {

            System.out.println("人脸不匹配");
        }
    }


    /**
     * OpenCV-4.7.0 从摄像头实时读取
     */
    public static void getVideoFromCamera() {
        //1 如果要从摄像头获取视频 则要在 VideoCapture 的构造方法写 0
        VideoCapture capture = new VideoCapture(0);
        Mat video = new Mat();
        int index = 0;
        if (capture.isOpened()) {

            while (i < 3) {
                // 匹配成功3次退出
                capture.read(video);
                HighGui.imshow("实时人脸识别", getFace(video));
                index = HighGui.waitKey(100);
                if (index == 27) {
                    capture.release();
                    break;
                }
            }
        } else {

            System.out.println("摄像头未开启");
        }
        try {

            capture.release();
            Thread.sleep(1000);
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * OpenCV-4.7.0 从视频文件中读取
     */
    public static void getVideoFromFile() {

        VideoCapture capture = new VideoCapture();
        //capture.open(PATH_PREFIX + "/yimi.mp4");//1 读取视频文件的路径
        capture.open("C:\\Users\\ANDIUS\\Desktop\\yimi.mp4");
        if (!capture.isOpened()) {

            System.out.println("读取视频文件失败！");
            return;
        }
        Mat video = new Mat();
        int index = 0;
        while (capture.isOpened()) {

            capture.read(video);//2 视频文件的视频写入 Mat video 中
            HighGui.imshow("本地视频识别人脸", getFace(video));//3 显示图像
            index = HighGui.waitKey(100);//4 获取键盘输入
            if (index == 27) {
                //5 如果是 Esc 则退出
                capture.release();
                return;
            }
        }
    }

    /**
     * OpenCV-4.7.0 人脸识别
     *
     * @param image 待处理Mat图片(视频中的某一帧)
     * @return 处理后的图片
     */
    public static Mat getFace(Mat image) {
        // 1 读取OpenCV自带的人脸识别特征XML文件(faceDetector)
        CascadeClassifier facebook = new CascadeClassifier("src/main/java/opencv/model/haarcascade_frontalface_alt.xml");
        // 2 特征匹配类
        MatOfRect face = new MatOfRect();
        // 3 特征匹配
        faceDetector.detectMultiScale(image, face);
        Rect[] rects = face.toArray();
        System.out.println("匹配到 " + rects.length + " 个人脸");
        if (rects != null && rects.length >= 1) {

            // 4 为每张识别到的人脸画一个圈
            for (int i = 0; i < rects.length; i++) {

                Imgproc.rectangle(image, new Point(rects[i].x, rects[i].y), new Point(rects[i].x + rects[i].width, rects[i].y + rects[i].height), new Scalar(0, 255, 0));
                Imgproc.putText(image, " ", new Point(rects[i].x, rects[i].y), Imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX, 1.0, new Scalar(0, 255, 0), 1, Imgproc.LINE_AA, false);
                Mat dst=image.clone();
                Imgproc.resize(image, image, new Size(300,300));
            }
            i++;
            if (i == 3) {
                // 获取匹配成功第10次的照片
                Imgcodecs.imwrite(PATH_PREFIX + "/face.png", image);
            }
        }
        return image;
    }


    /**
     * OpenCV-4.7.0 图片人脸识别
     */
    public static void face(String filename) {
        // 1 读取OpenCV自带的人脸识别特征XML文件
        // OpenCV 图像识别库一般位于 opencv\sources\data 下面
        CascadeClassifier facebook=new CascadeClassifier("src/main/java/opencv/model/haarcascade_frontalface_alt.xml");
        // 2 读取测试图片
        String imgPath = PATH_PREFIX + filename;
        Mat image = Imgcodecs.imread(imgPath);
        if (image.empty()) {

            System.out.println("image 内容不存在！");
            return;
        }
        // 3 特征匹配
        MatOfRect face = new MatOfRect();
        faceDetector.detectMultiScale(image, face);
        // 4 匹配 Rect 矩阵 数组
        Rect[] rects = face.toArray();
        System.out.println("匹配到了" + rects.length + " 个人脸");
        // 5 为每张识别到的人脸画一个圈
        int i = 1;
        for (Rect rect : face.toArray()) {

            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0), 3);
            imageCut(imgPath, PATH_PREFIX + i + ".jpg", rect.x, rect.y, rect.width, rect.height);// 进行图片裁剪
            i++;
        }
        // 6 展示图片
        HighGui.imshow("人脸识别", image);
        HighGui.waitKey(0);
    }

    /**
     * 裁剪人脸
     *
     * @param imagePath
     * @param outFile
     * @param posX
     * @param posY
     * @param width
     * @param height
     */
    public static void imageCut(String imagePath, String outFile, int posX, int posY, int width, int height) {
        // 原始图像
        Mat image = Imgcodecs.imread(imagePath);
        // 截取的区域：参数,坐标X,坐标Y,截图宽度,截图长度
        Rect rect = new Rect(posX, posY, width, height);
        // 两句效果一样
        Mat sub = image.submat(rect); // Mat sub = new Mat(image, rect);
        Mat mat = new Mat();
        Size size = new Size(width, height);
        Imgproc.resize(sub, mat, size);// 将人脸进行截图并保存
        Imgcodecs.imwrite(outFile, mat);
        System.out.println(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
    }

    /**
     * 人脸比对
     *
     * @param img_1
     * @param img_2
     * @return
     */
    public static double compare_image(String img_1, String img_2) {

        Mat mat_1 = conv_Mat(img_1);
        Mat mat_2 = conv_Mat(img_2);
        Mat hist_1 = new Mat();
        Mat hist_2 = new Mat();

        //颜色范围
        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        //直方图大小， 越大匹配越精确 (越慢)
        MatOfInt histSize = new MatOfInt(1000);

        Imgproc.calcHist(Arrays.asList(mat_1), new MatOfInt(0), new Mat(), hist_1, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(mat_2), new MatOfInt(0), new Mat(), hist_2, histSize, ranges);

        // CORREL 相关系数
        double res = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CORREL);
        return res;
    }

    /**
     * 灰度化人脸
     *
     * @param img
     * @return
     */
    public static Mat conv_Mat(String img) {

        Mat image0 = Imgcodecs.imread(img);

        Mat image1 = new Mat();
        // 灰度化
        Imgproc.cvtColor(image0, image1, Imgproc.COLOR_BGR2GRAY);
        // 探测人脸
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image1, faceDetections);
        // rect中人脸图片的范围
        for (Rect rect : faceDetections.toArray()) {

            Mat face = new Mat(image1, rect);
            return face;
        }
        return null;
    }

    /**
     * OpenCV-4.7.0 将摄像头拍摄的视频写入本地
     */
    public static void writeVideo() {

        //1 如果要从摄像头获取视频 则要在 VideoCapture 的构造方法写 0
        VideoCapture capture = new VideoCapture(0);
        Mat video = new Mat();
        int index = 0;
        Size size = new Size(capture.get(Videoio.CAP_PROP_FRAME_WIDTH), capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        VideoWriter writer = new VideoWriter("C:\\Users\\ANDIUS\\Desktop\\AndiusIPS(Information Processing System).24.0.0\\OpenCVdemo\\src\\main\\resources", VideoWriter.fourcc('D', 'I', 'V', 'X'), 15.0, size, true);
        while (capture.isOpened()) {

            capture.read(video);//2 将摄像头的视频写入 Mat video 中
            writer.write(video);
            HighGui.imshow("像头获取视频", video);//3 显示图像
            index = HighGui.waitKey(100);//4 获取键盘输入
            if (index == 27) {
                //5 如果是 Esc 则退出
                capture.release();
                writer.release();
                return;
            }
        }
    }

}
