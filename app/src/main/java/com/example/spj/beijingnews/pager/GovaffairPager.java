package com.example.spj.beijingnews.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.adapter.GovaffairPagerAdapter;
import com.example.spj.beijingnews.base.BasePager;
import com.example.spj.beijingnews.domain.ShoppingCart;
import com.example.spj.beijingnews.pay.PayResult;
import com.example.spj.beijingnews.pay.SignUtils;
import com.example.spj.beijingnews.utils.CartProvider;
import com.example.spj.beijingnews.utils.LogUtil;
import com.example.spj.beijingnews.view.MyItemDecoration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by spj on 2016/8/15.
 */
public class GovaffairPager extends BasePager {

    private CartProvider cartProvider;
    private RecyclerView recyclerview;
    private CheckBox checkbox_all;
    private TextView tv_total_price;
    private Button btn_order;
    private Button btn_delete;
    private List<ShoppingCart> carts;
    private GovaffairPagerAdapter adapter;
    private TextView tv_result;
    /**
     * 编辑状态
     */
    private static final int ACTION_EDIT = 0;
    /**
     * 完成状态
     */
    private static final int ACTION_COMPLETE = 1;

    // 商户PID
    public static final String PARTNER = "2088911876712776";
    // 商户收款账号
    public static final String SELLER = "chenlei@atguigu.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALibQNKhOPy4ipgo\n" +
            "DKFhaEMR5tl8niTaUHN67ePbA918ju/CZpyTEzOgNemwPi4htPTygQTiGh+5g95h\n" +
            "i2srZmv3hh98IuOGfrBgqe5rBoyW91EQ9WLFxglslfPBDdpjtWnVPGtMFEO4Gcsa\n" +
            "W2QOH9WjOTkRH/ZobUyX4cKrdg47AgMBAAECgYBb4dIqw9nkV0mMH+rha+UD9OYM\n" +
            "e60OtKZ0Q8whq8HJvJQ8G2sNJVraSDRtQq5AMcCqZgT3VD4iaiiLR8unpKtN2jmM\n" +
            "SPZr/OjxHYpftHXKJSPEhQKl5gE9pk0Fe+0KZueX8vxs7aSz8c/k763paPKuE6VP\n" +
            "q4xLyTBrX5v3zNAXUQJBAO8y7xI8J/Ghth4Zp8oKsCjTStvnPOVfeyjr26P6ZHUE\n" +
            "Fap2Bpt5zWVC7B2PtBgQv/mCO/7TMbZ0S7u/KK0GVRMCQQDFkq9Ol4d7jaffsjWj\n" +
            "idh/tKU8np47Mfe9XGoepDvNIkXvDB4GNNjcg3Rjj53bQ7bgukA8xEUig5kRQcFw\n" +
            "yw85AkEAjJo481QO+rbesUTNXzL3J5hDyY1cO0vrvjsdyX62rB3xiliEO8HWHS7A\n" +
            "UgQVjYvS/Jw0He8QqrojhkGwbDxrpwJADjbWa/YU5juzxzFAEKr7K3zoomrbAXE/\n" +
            "3JzIebnhH4oGtAMQKewlaf//IKaVec/uWU6tDnIkcy46lfZAH4hMaQJADDpe7bqi\n" +
            "jbaAPkktdbiUcB/htAdr5l7UiRTnhwmNyJNUry6hLLBTR+Cy2XHMn7BZ3yybmhMP\n" +
            "13MqyaCyA9F1Eg==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4m0DSoTj8uIqYKAyhYWhDEebZ\n" +
            "fJ4k2lBzeu3j2wPdfI7vwmackxMzoDXpsD4uIbT08oEE4hofuYPeYYtrK2Zr94Yf\n" +
            "fCLjhn6wYKnuawaMlvdREPVixcYJbJXzwQ3aY7Vp1TxrTBRDuBnLGltkDh/Vozk5\n" +
            "ER/2aG1Ml+HCq3YOOwIDAQAB";
    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };


    public GovaffairPager(Context context) {
        super(context);
        cartProvider = new CartProvider(context);
    }

    @Override
    public void initData() {
        super.initData();
        //设置标题
        tv_title.setText("商品结算");

        //联网请求，得到数据，创建视图
        btn_cart.setVisibility(View.VISIBLE);
        //2.联网请求，得到数据，创建视图

        View view = View.inflate(mContext, R.layout.govaffair_pager, null);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        checkbox_all = (CheckBox) view.findViewById(R.id.checkbox_all);
        tv_total_price = (TextView) view.findViewById(R.id.tv_total_price);
        btn_order = (Button) view.findViewById(R.id.btn_order);
        btn_delete = (Button) view.findViewById(R.id.btn_delete);
        tv_result = (TextView) view.findViewById(R.id.tv_result);
        //3.把子视图添加到BasePager的FrameLayout中
        if (fl_content != null) {
            fl_content.removeAllViews();
        }
        fl_content.addView(view);

        //设置RecycleView的分割线
        recyclerview.addItemDecoration(new MyItemDecoration(mContext,MyItemDecoration.VERTICAL_LIST));

        //设置编辑的点击事件
        btn_cart.setText("编辑");
        btn_cart.setTag(ACTION_EDIT);
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int action = (int) btn_cart.getTag();
                LogUtil.e("action============= " + action);
                if (action == ACTION_EDIT) {
                    //把它变成完成状态,可以删除
                    showDelete();
                } else if (action == ACTION_COMPLETE) {
                    //把它变成编辑状态，可以结算
                    showPay();
                }
            }
        });

        //设置删除按钮的点击
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.deleteData();
                adapter.showTotalPrice();
                checkData();
                adapter.checkAll();
            }
        });

        //点击结算按钮调用支付宝
        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pay(view);
            }
        });

        showData();
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    public void pay(View v) {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(mContext).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
//                            finish();
                        }
                    }).show();
            return;
        }
        String orderInfo = getOrderInfo("尚硅谷商城购物", "买了好多东西", adapter.getToalPrice()+"");

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) mContext);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
    /**
     * create the order info. 创建订单信息
     *
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    //将购物车的文本信息设置隐藏或者显示
    private void checkData() {
        if(adapter != null && adapter.getItemCount()>0) {
            //有数据
                tv_result.setVisibility(View.GONE);
           }else {
            //没有数据
            tv_result.setVisibility(View.VISIBLE);
        }
    }

    private void showPay() {
        //1.文本设置-编辑
         btn_cart.setText("编辑");
        //2.状态设置
        btn_cart.setTag(ACTION_EDIT);
       //3.数据设置全选
        adapter.checkAll_none(true);
        adapter.checkAll();
        //4.隐藏删除按钮，结算按钮
        btn_delete.setVisibility(View.GONE);
        btn_order.setVisibility(View.VISIBLE);
        //5.价格重新计算
        adapter.showTotalPrice();
    }

    private void showDelete() {
        //1.文本设置-完成
        btn_cart.setText("完成");
        //2.状态设置
        btn_cart.setTag(ACTION_COMPLETE);
        //3.数据设置非全选
        adapter.checkAll_none(false);
        adapter.checkAll();
        //4.显示删除按钮，隐藏结算按钮
        btn_delete.setVisibility(View.VISIBLE);
        btn_order.setVisibility(View.GONE);
        //5.重新计算价格
        adapter.showTotalPrice();
    }

    private void showData() {
        carts = cartProvider.getAllData();
        if (carts != null && carts.size() > 0) {
            //有数据
            tv_result.setVisibility(View.GONE);
            //设置适配器
            adapter = new GovaffairPagerAdapter(mContext,carts,checkbox_all,tv_total_price);
            recyclerview.setAdapter(adapter);
            recyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        } else {
            //没有数据
           tv_result.setVisibility(View.VISIBLE);
        }

    }
}
