package com.example.sample4

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    var username:String = ""
    var token:String = "mytoken"
    var b = booleanArrayOf(false, false)
    var a = booleanArrayOf(false, false)
    var c = booleanArrayOf(false, false, false)
    var d = booleanArrayOf(false, false)
    var list: ListView? = null
    var ids = ArrayList<Int>()
    var classnames = ArrayList<String>()
    var role = ArrayList<String>()
    var tits = ArrayList<String>()
    var Descs = ArrayList<String>()
    var saw = ArrayList<Boolean>()
    var idnon = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }
    init {
        instance = this
    }
    companion object {
        private var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
    override fun onStart() {
        super.onStart()
        var shared = Shared(applicationContext)
        shared.firsttime()
        Log.d("USERNAME", shared.sharedPreferences.getString(shared.Filename, "NONE").toString())
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this@MainActivity, object : OnSuccessListener<InstanceIdResult> {
            override fun onSuccess(instanceIdResult: InstanceIdResult) {
                val newToken = instanceIdResult.getToken()
                Log.d("NEWTOKEN", newToken)
                if (shared.sharedPreferences.getBoolean(shared.Data, false).equals(true))
                    sendPost(shared.sharedPreferences.getString(shared.Filename, "NONE").toString(), newToken)
                else
                    return
                var ctr = 0
                while (!b[1]) {
                    ctr++
                    if (ctr > 2500000000) break
                }
                if (ctr > 2500000000) {
                    Toast.makeText(baseContext, "Unable to Send Token", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(baseContext, "Sent Token Successfully", Toast.LENGTH_SHORT).show()
                }
            }
        })
        val usr = findViewById(R.id.usrid) as TextView
        usr.setText(shared.sharedPreferences.getString(shared.Filename, "NONE").toString())
        Log.d("Flag", shared.sharedPreferences.getBoolean(shared.Data, false).toString())
        val logout_btn = findViewById<Button>(R.id.btn_logout) as Button
        logout_btn.setOnClickListener(View.OnClickListener {
            logoutreq(shared.sharedPreferences.getString(shared.Filename, "NONE").toString())
            shared.editor.putString(shared.Filename, "NONE")
            shared.editor.putBoolean(shared.Data, false)
            shared.editor.commit()
            shared.firsttime()
        })
        if(shared.sharedPreferences.getBoolean(shared.Data, false).equals(true))
            getClasses()
        else
            return
    }

    private fun logoutreq(s:String) {
        var obj = JSONObject()
        try {
            obj.put("username", s)
        } catch (e: JSONException) {
            Log.e("JSONERROR", e.toString())
            e.printStackTrace()
        }
        val thread = Thread {
            try {
                val url = URL("http://paavankumar.pythonanywhere.com/logout_student")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doInput = true
                Log.i("JSON", obj.toString())
                val os = DataOutputStream(conn.outputStream)
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(obj.toString())
                os.flush()
                os.close()
                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)
                if (conn.responseCode.toString() == "200")
                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun getClasses() {
        list = findViewById(R.id.list1) as ListView
        getclass()
        var ctr = 0
        //a[1] = false
        //a[0] = false
        while (!a[1]) {
            ctr++
            if (ctr > 2000000000) break
        }
        if (ctr > 2000000000) {
            Toast.makeText(baseContext, "Request Time Out", Toast.LENGTH_LONG).show()
        }
        else
        {
            Toast.makeText(baseContext, "Fetched Classes Successfully", Toast.LENGTH_SHORT).show()
        }
        val adapter = MyAdapter(this, classnames, ids, role)
        list!!.setAdapter(adapter)
        list!!.setOnItemClickListener { parent, view, position, id ->
            val element = adapter.getNAMEAtPosition(position) // The item that was clicked
            val id = adapter.getIDAtPosition(position) as Int
            val role = adapter.getROLEAtPosition(position) as String
            Log.d("ELEMENT", element as String)
            tits = ArrayList<String>()
            Descs = ArrayList<String>()
            saw = ArrayList<Boolean>()
            idnon = ArrayList<Int>()
            getEvents(element, id, role)
        }
    }

    private fun getclass(){
        var obj = JSONObject()
        var shared = Shared(applicationContext)
        try {
            obj.put("name", shared.sharedPreferences.getString(shared.Filename, "NONE").toString())
        } catch (e: JSONException) {
            Log.e("JSONERROR", e.toString())
            e.printStackTrace()
        }
        val thread = Thread {
            try {
                val url = URL("http://paavankumar.pythonanywhere.com/get_classes")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.doInput = true
                Log.i("JSON", obj.toString())
                val os = DataOutputStream(conn.outputStream)
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(obj.toString())
                os.flush()
                os.close()
                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                val sb = StringBuilder()
                var output: String?
                while (br.readLine().also { output = it } != null) {
                    sb.append(output)
                }
                Log.d("JSON", sb.toString())
                obj = JSONObject(sb.toString())
                jsontoArray1(obj)
                if (conn.responseCode.toString() == "200") a[0] = true
                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun jsontoArray1(obj: JSONObject) {
        Log.d("DATA", obj.getString("data1").toString())
        var idJson = obj.getJSONArray("data1")
        ids = ArrayList<Int>()
        classnames = ArrayList<String>()
        role = ArrayList<String>()
        for (i in 0..idJson!!.length() - 1) {
            val id = idJson.getJSONObject(i).getString("id")
            val clasnam = idJson.getJSONObject(i).getString("name")
            Log.d("ID", id)
            Log.d("CLASSNAME", clasnam)
            ids.add(id.toInt())
            classnames.add(clasnam)
            role.add("student")
        }
        idJson = obj.getJSONArray("data2")
        for (i in 0..idJson!!.length() - 1) {
            val id = idJson.getJSONObject(i).getString("id")
            val clasnam = idJson.getJSONObject(i).getString("name")
            Log.d("ID", id)
            Log.d("CLASSNAME", clasnam)
            ids.add(id.toInt())
            classnames.add(clasnam)
            role.add("TA")
        }
        a[1] = true;
    }

    private fun getEvents(el: String, id: Int, rolex: String) {
        c[2] = false
        getevent(id, rolex, el)
        var ctr = 0
        while (!c[2]) {
            ctr++
            if (ctr > 1000000000) break
        }
        if(tits.isEmpty())
        {
            Toast.makeText(baseContext, "Uh!Oh Nothing to Display", Toast.LENGTH_SHORT).show()
            return
        }
        list = findViewById(R.id.list1) as ListView
        val adepter = MyAdapter2(this, tits, Descs, el, saw, idnon)
        list!!.setAdapter(adepter)
        list!!.setOnItemClickListener { parent, view, position, id ->
            val id = adepter.getIDAtPosition(position) as Int // The item that was clicked
            val sw = adepter.getSEENATPOSITION(position)
            Log.d("ELEMENT", el as String)
            if (sw as Boolean)
                getClasses()
            else {
                d[1] = false
                sendseen(id)
                var ctri = 0
                while (!d[1]) {
                    ctri++
                    if (ctri > 2000000000) break
                }
                if (ctri > 2000000000) {
                    Toast.makeText(baseContext, "Request Time Out", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(baseContext, "Acknowledgement Successful", Toast.LENGTH_LONG).show()
                    val seen = view.findViewById<TextView>(R.id.setseen)
                    seen.text = "✓✓"
                    saw[position] = true
                }
            }
        }
    }

    private fun sendseen(id: Int) {
        var obj = JSONObject()
        var shared = Shared(applicationContext)
        try {
            obj.put("username", shared.sharedPreferences.getString(shared.Filename, "NONE").toString())
            obj.put("id", id)
        } catch (e: JSONException) {
            Log.e("JSONERROR", e.toString())
            e.printStackTrace()
        }
        val thread = Thread {
            try {
                val url = URL("http://paavankumar.pythonanywhere.com/seen_notif")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doInput = true
                Log.i("JSON", obj.toString())
                val os = DataOutputStream(conn.outputStream)
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(obj.toString())
                os.flush()
                os.close()
                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)
                if (conn.responseCode.toString() == "200") d[0] = true
                conn.disconnect()
                d[1] = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun getevent(id: Int, role: String, el: String) {
        c[1] = false
        c[0] = false
        c[2] = false
        var obj = JSONObject()
        var shared = Shared(applicationContext)
        try {
            obj.put("username", shared.sharedPreferences.getString(shared.Filename, "NONE").toString())
            obj.put("id", id)
            obj.put("role", role)
        } catch (e: JSONException) {
            Log.e("JSONERROR", e.toString())
            e.printStackTrace()
        }
        val thread = Thread {
            try {
                val url = URL("http://paavankumar.pythonanywhere.com/get_notifications")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.doInput = true
                Log.i("JSON", obj.toString())
                val os = DataOutputStream(conn.outputStream)
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(obj.toString())
                os.flush()
                os.close()
                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                val sb = StringBuilder()
                var output: String?
                while (br.readLine().also { output = it } != null) {
                    sb.append(output)
                }
                Log.d("JSON", sb.toString())
                val ob = JSONArray(sb.toString())
                jsontoArray2(ob, el)
                if (conn.responseCode.toString() == "200") c[0] = true
                conn.disconnect()
                c[1] = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun jsontoArray2(obj: JSONArray, el: String) {
        var ctrp = 0
        while (!c[1]) {
            ctrp++
            if (ctrp > 1000) break
        }
        Log.d("NOTIF", obj.toString())
        for (i in 0..obj!!.length() - 1) {
            val id = obj.getJSONObject(i).getString("id")
            Log.d("IDNON", id)
            idnon.add(id.toInt())
            tits.add(obj.getJSONObject(i).getString("topic"))
            Descs.add(obj.getJSONObject(i).getString("body"))
            saw.add(obj.getJSONObject(i).getBoolean("seen"))
        }
        c[2] = true
        Log.d("MYTITS", tits.toString())
    }

    internal class MyAdapter(context: Context, var myTitles: ArrayList<String>, var myids: ArrayList<Int>, var myrole: ArrayList<String>) : ArrayAdapter<String?>(context, R.layout.row, R.id.text1, myTitles as List<String?>) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater = applicationContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val row = layoutInflater.inflate(R.layout.row, parent, false)
            val images = row.findViewById<ImageView>(R.id.logo)
            val myTitle = row.findViewById<TextView>(R.id.text1)
            val myDescription = row.findViewById<TextView>(R.id.text2)
            images.setImageResource(R.drawable.image)
            myTitle.text = myTitles[position]
            myDescription.text = "As a "+myrole[position]
            return row
        }

        fun getIDAtPosition(position: Int): Any {
            return myids[position]
        }
        fun getNAMEAtPosition(position: Int): Any {
            return myTitles[position]
        }
        fun getROLEAtPosition(position: Int): Any {
            return myrole[position]
        }
    }

    internal class MyAdapter2(context: Context, var myTitles: ArrayList<String>, var myDescriptions: ArrayList<String>, var cla: String, var saw: ArrayList<Boolean>, var idnon: ArrayList<Int>) : ArrayAdapter<String?>(context, R.layout.event, R.id.text1event, myTitles as List<String?>) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater = applicationContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val row = layoutInflater.inflate(R.layout.event, parent, false)
            val clr = row.findViewById<TextView>(R.id.textevent)
            val myTitle = row.findViewById<TextView>(R.id.text1event)
            val myDescription = row.findViewById<TextView>(R.id.text2event)
            val seen = row.findViewById<TextView>(R.id.setseen)
            clr.text = cla
            Log.d("TiTS", myTitles.toString())
            myTitle.text = myTitles[position]
            myDescription.text = myDescriptions[position]
            if(!saw[position])
                seen.text = "~"
            else
                seen.text = "✓✓"
            return row
        }

        fun getIDAtPosition(position: Int): Any {
            return idnon[position]
        }
        fun getSEENATPOSITION(position: Int): Any {
            return saw[position]
        }
    }

    fun sendPost(s1: String, s2: String) {
        val obj = JSONObject()
        try {
            obj.put("username", s1)
            obj.put("token", s2)
        } catch (e: JSONException) {
            Log.e("JSONERROR", e.toString())
            e.printStackTrace()
        }
        val thread = Thread {
            try {
                val url = URL("http://paavankumar.pythonanywhere.com/add_token")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.doInput = true
                Log.i("JSON", obj.toString())
                val os = DataOutputStream(conn.outputStream)
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(obj.toString())
                os.flush()
                os.close()
                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                val sb = StringBuilder()
                var output: String?
                while (br.readLine().also { output = it } != null) {
                    sb.append(output)
                }
                Log.d("JSON", sb.toString())
                if (conn.responseCode.toString() == "200") b[0] = true
                conn.disconnect()
                b[1] = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
        Log.d("FLAG", b[0].toString())
    }
}