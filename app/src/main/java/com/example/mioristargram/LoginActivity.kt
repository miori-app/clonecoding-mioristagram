package com.example.mioristargram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    //Firebase Authentication 관리 클래스스
    // 회원가입
    var auth: FirebaseAuth? = null //lib 불러오기
    var googleSignInClient : GoogleSignInClient? = null //class
    var GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        //button event
        email_login_button.setOnClickListener {
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener {
            // First step
            googleLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) //google api key
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso) //option 값 setting
    }
    //google social login
    // google login -> firebase (server flatform) -> response of Login
    // dependency "play-services-auth" -> com.google.android.gms
    fun googleLogin(){
        var signInInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInInIntent,GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data) //google 에서 주는 결과값
            if (result.isSuccess){
                var account = result.signInAccount
                //second
                firebaseAuthWithGoogle(account)
            }
        }
    }
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null) //account 내 token id 넘겨
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //id pw 맞을때
                    moveMainPage(task.result?.user)
                } else {
                    // login 실패
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }

            }
    }
    //Signup
    //addOnCompleteLister 결과값 받아오고 task가 parameter
    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //계정 생성 성공
                    moveMainPage(task.result?.user)
                } else if (!task.exception?.message.isNullOrEmpty()) {
                    // login error , error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    // login 으로
                    signinEmail()
                }

            }
    }
    //Login
    fun signinEmail() {
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //id pw 맞을때
                    moveMainPage(task.result?.user)
                } else {
                    // login 실패
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }

            }
    }
    // Login 성공시 다음 page
    fun moveMainPage(user: FirebaseUser?) {
        // firebase user 상태 넘겨줘
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
