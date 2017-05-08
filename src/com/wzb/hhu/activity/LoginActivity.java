package com.wzb.hhu.activity;

import com.wzb.hhu.R;
import com.wzb.hhu.util.CustomDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date May 3, 2017 2:50:41 PM	
 */
public class LoginActivity extends BaseActivity {  
    
    private EditText userName, password;  
    private CheckBox rem_pw, auto_login;  
    private Button btn_login;   
    private String userNameValue,passwordValue;  
    private SharedPreferences sp;  
  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
          
        //去除标题  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.login);  
          
        //获得实例对象  
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);  
        userName = (EditText) findViewById(R.id.et_acc);  
        password = (EditText) findViewById(R.id.et_ps);  
        rem_pw = (CheckBox) findViewById(R.id.cb_ps);  
        auto_login = (CheckBox) findViewById(R.id.cb_auto);  
        btn_login = (Button) findViewById(R.id.btn_login);  
          
          
        //判断记住密码多选框的状态  
      if(sp.getBoolean("ISCHECK", false))  
        {  
          //设置默认是记录密码状态  
          rem_pw.setChecked(true);  
          userName.setText(sp.getString("USER_NAME", ""));  
          password.setText(sp.getString("PASSWORD", ""));  
          //判断自动登陆多选框状态  
          if(sp.getBoolean("AUTO_ISCHECK", false))  
          {  
                 //设置默认是自动登录状态  
                 auto_login.setChecked(true);  
                //跳转界面  
               // Intent intent = new Intent(LoginActivity.this,LogoActivity.class);  
               // LoginActivity.this.startActivity(intent);  
                  
          }  
        }  
          
        // 登录监听事件  现在默认为用户名为：liu 密码：123  
        btn_login.setOnClickListener(new OnClickListener() {  
  
            public void onClick(View v) {  
                userNameValue = userName.getText().toString();  
                passwordValue = password.getText().toString();  
                  
                if(userNameValue.equals("liu")&&passwordValue.equals("123"))  
                {  
                    Toast.makeText(LoginActivity.this,"登录成功", Toast.LENGTH_SHORT).show();  
                    //登录成功和记住密码框为选中状态才保存用户信息  
                    if(rem_pw.isChecked())  
                    {  
                     //记住用户名、密码、  
                      Editor editor = sp.edit();  
                      editor.putString("USER_NAME", userNameValue);  
                      editor.putString("PASSWORD",passwordValue);  
                      editor.commit();  
                    }  
                    //跳转界面  
                  //  Intent intent = new Intent(LoginActivity.this,LogoActivity.class);  
                   // LoginActivity.this.startActivity(intent);  
                    //finish();  
                      
                }else{  
                      
                    Toast.makeText(LoginActivity.this,"用户名或密码错误，请重新登录", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class); 
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    LoginActivity.this.startActivity(intent);  
                    finish(); 
                   
                }  
                  
            }  
        });  
  
        //监听记住密码多选框按钮事件  
        rem_pw.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
                if (rem_pw.isChecked()) {  
                      
                    System.out.println("记住密码已选中");  
                    sp.edit().putBoolean("ISCHECK", true).commit();  
                      
                }else {  
                      
                    System.out.println("记住密码没有选中");  
                    sp.edit().putBoolean("ISCHECK", false).commit();  
                      
                }  
  
            }  
        });  
          
        //监听自动登录多选框事件  
        auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
                if (auto_login.isChecked()) {  
                    System.out.println("自动登录已选中");  
                    sp.edit().putBoolean("AUTO_ISCHECK", true).commit();  
  
                } else {  
                    System.out.println("自动登录没有选中");  
                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();  
                }  
            }  
        });  
          
  
    }  
}
