package com.vcredit.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.vcredit.app.R;
import com.vcredit.app.entities.SysEnumInfo;
import com.vcredit.global.App;
import com.vcredit.global.AppConfig;
import com.vcredit.service.DownloadService;

import org.apache.commons.lang3a.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by wangzhengji on 2016/1/26.
 */
public class CommonUtils {
    // 自定义log参数
    private static final String LOG_TAG = "StarCredit";
    private static final int LOG_SIZE_LIMIT = 3500;

    // 定时器参数
    private static Button mBtn;
    private static int mTime;
    private static Handler handler = new Handler();
    private static boolean isSending = false;
    private static boolean mallowgetCode;
    private static boolean mispress;

    public static int LOG_D(Class<?> paramClass, Object...msg) {
        String message = null;
        if (ArrayUtils.isEmpty(msg)) {
            message = "null or empty msg!";
        }else{
            try {
                message = String.format(msg[0].toString(), ArrayUtils.subarray(msg, 1, msg.length));
            } catch (Exception e) {
                message = msg.toString();
            }
        }

        return LOG_D(paramClass, message);
    }

    /**
     * 统一自定义log，建议使用
     *
     * @param paramClass getClass()或xxx.class
     * @param param      需要打印Object
     */
    public static int LOG_D(Class<?> paramClass, Object param) {
        // 只有debug模式才打印log
        if (AppConfig.DEBUG) {
            String paramString = param.toString();
            String str = paramClass.getName();
            if (str != null) {
                str = str.substring(1 + str.lastIndexOf("."));
            }
            int i = paramString.length();
            if (i > LOG_SIZE_LIMIT) {
                int j = 0;
                int k = 1 + i / LOG_SIZE_LIMIT;
                while (j < k + -1) {
                    Log.d(LOG_TAG, paramString.substring(j * LOG_SIZE_LIMIT,
                            LOG_SIZE_LIMIT * (j + 1)));
                    j++;
                }
                return Log.d(LOG_TAG, paramString.substring(j * LOG_SIZE_LIMIT, i));
            } else {
                return Log.d(LOG_TAG, str + " -> " + paramString);
            }
        }
        return 0;
    }

    /**
     * 获取安装包信息
     *
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo packageInfo = new PackageInfo();
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 判断Sdcard是否存在
     *
     * @return
     */
    public static boolean detectSdcardIsExist() {
        String extStorageState = Environment.getExternalStorageState();
        File file = Environment.getExternalStorageDirectory();
        if (!Environment.MEDIA_MOUNTED.equals(extStorageState)
                || !file.exists() || !file.canWrite()
                || file.getFreeSpace() <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 判断指定路径下的文件是否存在
     */
    public static boolean detectFileIsExist(String path) {
        if (null != path) {
            File file = new File(path);
            if (file.exists()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获得当前时间
     *
     * @return
     */
    public static String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    public static int Dp2Px(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * px转dp
     *
     * @param px
     * @return
     */
    public static int Px2Dp(Context context, float px) {
        return (int) (px / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 获取IMEI
     * 手机唯一设别号码
     */
    public static String getIMEI(Context context) {
        if (null == context) {
            return null;
        }
        String imei = null;
        try {
            imei = new DeviceUuidFactory(context).getDeviceUuid().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    /**
     * 判断字符串是否为空或NULL
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断集合对象是否为空或NULL
     *
     * @param list
     * @return
     */
    public static boolean isNullOrEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * InputStream to String
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    /**
     * 创建通用单选列表
     *
     * @param contexts
     * @param listener
     * @param adapter
     * @param titleMessage
     * @param positiveMessage
     * @param positiveListener
     * @param negativeMessage
     * @param negativeListener
     * @return
     */
    public static AlertDialog createDialog(Context contexts,
                                           DialogInterface.OnClickListener listener,
                                           ListAdapter adapter,
                                           String titleMessage,
                                           String positiveMessage,
                                           DialogInterface.OnClickListener positiveListener,
                                           String negativeMessage,
                                           DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(contexts);
        // builder.setSingleChoiceItems(items, 0, listener);

        builder.setAdapter(adapter, listener);
        builder.setTitle(titleMessage);
        builder.setPositiveButton(positiveMessage, positiveListener);
        builder.setNegativeButton(negativeMessage, negativeListener);
        return builder.create();
    }

    /**
     * 显示发送验证码的弹出框，使用默认的标题和hint,输入类型为数字、不自动弹出输入法
     *
     * @param context  显示AlertDialog的上下文对象
     * @param listener AlertDialog的事件监听
     * @return
     */
    public static AlertDialog showDynamicCodeDialog(Context context, final OnDynamicCodeDialogClickListener listener) {
        return showDynamicCodeDialog(context, null, null, listener);
    }

    /**
     * 显示发送验证码的弹出框,默认输入类型为数字、不自动弹出输入法,默认显示发送按钮
     *
     * @param context  显示AlertDialog的上下文对象
     * @param title    设置title，为null或者""时使用默认的
     * @param hint     设置验证码输入框的hint，为null时使用默认
     * @param listener AlertDialog的事件监听
     * @return
     */
    public static AlertDialog showDynamicCodeDialog(Context context, String title, String hint, final OnDynamicCodeDialogClickListener listener) {
        return showDynamicCodeDialog(context, title, hint, true, listener);
    }

    /**
     * 显示发送验证码的弹出框,默认输入类型为数字、不自动弹出输入法
     *
     *
     * @param context  显示AlertDialog的上下文对象
     * @param title    设置title，为null或者""时使用默认的
     * @param hint     设置验证码输入框的hint，为null时使用默认
     * @param hasButton 是否显示发送按钮
     * @param listener AlertDialog的事件监听
     * @return
     */
    public static AlertDialog showDynamicCodeDialog(Context context, String title, String hint, boolean hasButton, final OnDynamicCodeDialogClickListener listener) {
        return showDynamicCodeDialog(context, title, hint, false, hasButton, EditorInfo.TYPE_CLASS_NUMBER, listener);
    }


    /**
     * 显示发送验证码的弹出框
     *
     * @param context   显示AlertDialog的上下文对象
     * @param title     设置title，为null或者""时使用默认的
     * @param hint      设置验证码输入框的hint，为null时使用默认
     * @param inputAuto 是否自动弹出键盘
     * @param inputType 验证码输入框的输入类型
     * @param listener  AlertDialog的事件监听
     * @return
     */
    public static AlertDialog showDynamicCodeDialog(final Context context, String title, String hint, boolean inputAuto, boolean hasButton, int inputType, final OnDynamicCodeDialogClickListener listener) {
        final AlertDialog contractsDynamicCodeDialog = new AlertDialog.Builder(context).create();
        contractsDynamicCodeDialog.show();

        Window window = contractsDynamicCodeDialog.getWindow();
        window.setContentView(R.layout.dynamic_code_dialog);
        final TextView tvWithdrawCashDynamicDialogTitle = (TextView) window.findViewById(R.id.dialog_title);
        final Button btnWithdrawCashDynamicGetCode = (Button) window.findViewById(R.id.btn_dynamic_getCode);
        if (!hasButton)
            btnWithdrawCashDynamicGetCode.setVisibility(View.GONE);
        final EditText edtWithdrawCashDynamicCode = (EditText) window.findViewById(R.id.edt_dynamic_code);
        final TextView tvWithdrawCashDynamicDialogCancle = (TextView) window.findViewById(R.id.tv_dynamic_dialog_cancle);
        final TextView tvWithdrawCashDynamicDialogSure = (TextView) window.findViewById(R.id.tv_dynamic_dialog_sure);

        //设置标题和hint和输入框的类型
        if (!TextUtils.isEmpty(title))
            tvWithdrawCashDynamicDialogTitle.setText(title);
        if (hint != null)
            edtWithdrawCashDynamicCode.setHint(hint);
        edtWithdrawCashDynamicCode.setInputType(inputType);

        //设置按钮的点击事件
        if (listener != null) {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btn_dynamic_getCode:
                            listener.onGetCode(btnWithdrawCashDynamicGetCode, edtWithdrawCashDynamicCode);
                            break;
                        case R.id.tv_dynamic_dialog_cancle:
                            listener.onCancel(tvWithdrawCashDynamicDialogCancle, edtWithdrawCashDynamicCode);
                            break;
                        case R.id.tv_dynamic_dialog_sure:
                            if (TextUtils.isEmpty(edtWithdrawCashDynamicCode.getText())) {
                                TooltipUtils.showToastS(context, context.getString(R.string.verifycode_empty_tips));
                            }else {
                                listener.onSure(tvWithdrawCashDynamicDialogSure, edtWithdrawCashDynamicCode);
                            }
                            break;
                    }

                }
            };
            btnWithdrawCashDynamicGetCode.setOnClickListener(onClickListener);
            tvWithdrawCashDynamicDialogCancle.setOnClickListener(onClickListener);
            tvWithdrawCashDynamicDialogSure.setOnClickListener(onClickListener);
        }

        //Dialog隐藏的时候自动隐藏键盘
        contractsDynamicCodeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                InputTools.HideKeyboard(contractsDynamicCodeDialog.getWindow().getDecorView());
                if (listener != null)
                    listener.onDismiss(dialog);
            }
        });

        WindowManager.LayoutParams params = contractsDynamicCodeDialog.getWindow().getAttributes();
        //是否自动弹出键盘
        if (inputAuto) {
            params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
        }

        params.width = CommonUtils.Dp2Px(context, 300);
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致不能获取焦点,默认的是FLAG_NOT_FOCUSABLE,故名思义不能获取输入焦点
        contractsDynamicCodeDialog.getWindow().setAttributes(params);

        return contractsDynamicCodeDialog;
    }

    public interface OnDynamicCodeDialogClickListener {
        /**
         * 获取验证码
         *
         * @param view     获取验证码的按钮
         * @param editText 当前填写验证码的EditText
         */
        public void onGetCode(Button view, EditText editText);

        /**
         * 点击关闭按钮
         *
         * @param view     关闭按钮
         * @param editText 当前填写验证码的EditText
         */
        public void onCancel(TextView view, EditText editText);

        /**
         * 弹出框隐藏
         *
         * @param dialog
         */
        public void onDismiss(DialogInterface dialog);

        /**
         * 点击确定按钮
         *
         * @param view     确定按钮
         * @param editText 当前填写验证码的EditText
         */
        public void onSure(TextView view, EditText editText);
    }

    /**
     * 屏蔽手机号中间段
     *
     * @param replace
     * @return
     */
    public static String replaceWithAsteriskForPhone(@NonNull String replace) {
        if (TextUtils.isEmpty(replace)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(replace.substring(0, 3));
        for (int i = 3; i < replace.length() - 4; i++) {
            sb.append("*");
        }
        sb.append(replace.substring(replace.length() - 4));
        return sb.toString();
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        int versioncode = 0;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            versioncode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versioncode;
    }

    /**
     * 下载
     *
     * @param url      下载链接
     * @param context
     * @param fileName 文件名
     * @param size     文件大小（kb）
     */
    public static void startDownload(String url, Context context,
                                     String fileName, Float size) {
        Float tempFileSize = size * 1024;
        if (CommonUtils.detectSdcardIsExist()) {
            if (CommonUtils.isAvaiableSpace(tempFileSize)) {
                Intent intent = new Intent(context, DownloadService.class);
                intent.putExtra("downloadUrl", url);
                intent.putExtra("fileName", fileName);
                context.startService(intent);
                CommonUtils.LOG_D(CommonUtils.class, "startDownload");
            } else {
                TooltipUtils.showToastS(context, "存储卡空间不足");
            }
        } else {
            TooltipUtils.showToastS(context, "请检查存储卡是否安装");
        }
    }

    /**
     * 判断存储空间大小是否满足条件
     *
     * @param sizeByte
     * @return
     */
    public static boolean isAvaiableSpace(float sizeByte) {
        boolean ishasSpace = false;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String sdcard = Environment.getExternalStorageDirectory().getPath();
            StatFs statFs = new StatFs(sdcard);
            long blockSize = statFs.getBlockSize();
            long blocks = statFs.getAvailableBlocks();
            float availableSpare = blocks * blockSize;
            if (availableSpare > (sizeByte + 1024 * 1024)) {
                ishasSpace = true;
            }
        }
        return ishasSpace;
    }

    /**
     * 开始安装apk文件
     *
     * @param context
     * @param localFilePath
     */
    public static void installApkByGuide(Context context, String localFilePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + localFilePath),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 设置图片路径，缩略图最大宽度，从文件中读取图像数据并返回Bitmap对象
     *
     * @param filePath
     * @param maxWeight
     * @return
     */
    public static Bitmap reduce(String filePath, int maxWeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        float realWidth = options.outWidth;
        float realHeight = options.outHeight;
        float larger = (realWidth > realHeight) ? realWidth : realHeight;
        int scale = (int) (larger / maxWeight);
        if (scale <= 0) {
            scale = 1;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;

        bitmap = BitmapFactory.decodeFile(filePath, options);
        return bitmap;
    }

    public static String getNumFromString(String str) {
        str = str.trim();
        String str2 = "";
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2 += str.charAt(i);
                }
            }
        }
        return str2;
    }

    /**
     * @param imgPath 图片格式
     * @return
     */
    public static String imgToBase64(String imgPath) {
        if (detectFileIsExist(imgPath)) {
            // 将输入流转换为byte数组
            byte[] d = getByte(reduce(imgPath, 720));
            // 将这个输入流以Base64格式编码为String
            return Base64.encodeToString(d, Base64.NO_WRAP);
        }else {
            LOG_D(CommonUtils.class, "file(%s) not found ", imgPath);
            return "";
        }
    }

    /**
     * 将输入流转换为byte数组
     *
     * @param in
     * @return
     */
    public static byte[] getByte(Bitmap in) {
        if (in == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            in.compress(Bitmap.CompressFormat.JPEG, 95, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 将系统枚举转化成对象
     *
     * @return
     */
    public static SysEnumInfo analysisSysEnumsInfo() {
        String json = SharedPreUtils.getInstance(App.getInstance())
                .getValue(SharedPreUtils.APP_ENUM_INFO, "");
        if (TextUtils.isEmpty(json)){
            InputStream stream = App.getInstance().getClassLoader().getResourceAsStream("assets/sys_enum_info.txt");
            try {
                json = CommonUtils.inputStream2String(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SysEnumInfo sysEnumsInfo = JsonUtils.json2Object(json,
                SysEnumInfo.class);
        return sysEnumsInfo;
    }


    /**
     * 从左至右裁剪字符串
     *
     * @param prex        是否需添加字符串前缀
     * @param cutString   需进行裁剪的字符串
     * @param retainDigit 右侧需保留的字符个数
     * @return
     */
    public static String cutCharactersByLTR(String prex, String cutString, int retainDigit) {
        if (cutString == null || cutString.length() < retainDigit) {
            return "";
        }
        return prex + cutString.substring(cutString.length() - retainDigit);
    }
}
