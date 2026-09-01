package com.example.personelaifitness

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val API_URL = "http://10.0.2.2:5000/asistan"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etYas            = findViewById<TextInputEditText>(R.id.etYas)
        val etBoy            = findViewById<TextInputEditText>(R.id.etBoy)
        val etKilo           = findViewById<TextInputEditText>(R.id.etKilo)
        val etSporGecmisi    = findViewById<TextInputEditText>(R.id.etSporGecmisi)
        val rgCinsiyet       = findViewById<RadioGroup>(R.id.rgCinsiyet)
        val spinnerHedef     = findViewById<AutoCompleteTextView>(R.id.spinnerHedef)
        val spinnerEkipman   = findViewById<AutoCompleteTextView>(R.id.spinnerEkipman)
        val spinnerAktivite  = findViewById<AutoCompleteTextView>(R.id.spinnerAktivite)
        val spinnerOgun      = findViewById<AutoCompleteTextView>(R.id.spinnerOgun)
        val spinnerSakatlik  = findViewById<AutoCompleteTextView>(R.id.spinnerSakatlikBolgesi)
        val seekSakatlik     = findViewById<SeekBar>(R.id.seekSakatlik)
        val tvSakatlikSiddet = findViewById<TextView>(R.id.tvSakatlikSiddet)
        val btnAnalizEt      = findViewById<MaterialButton>(R.id.btnAnalizEt)

        // Gün seçim checkboxları
        val cbPazartesi  = findViewById<CheckBox>(R.id.cbPazartesi)
        val cbSali       = findViewById<CheckBox>(R.id.cbSali)
        val cbCarsamba   = findViewById<CheckBox>(R.id.cbCarsamba)
        val cbPersembe   = findViewById<CheckBox>(R.id.cbPersembe)
        val cbCuma       = findViewById<CheckBox>(R.id.cbCuma)
        val cbCumartesi  = findViewById<CheckBox>(R.id.cbCumartesi)
        val cbPazar      = findViewById<CheckBox>(R.id.cbPazar)

        spinnerHedef.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
            listOf("Güç ve Kondisyon", "Kas Kütlesi Kazanımı", "Yağ Yakımı", "Rehabilitasyon")))
        spinnerEkipman.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
            listOf("Full Gym", "Ev Jimnastiği", "Minimal Ekipman")))
        spinnerAktivite.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
            listOf(
                "Masa başı iş, egzersiz yok",
                "Haftada 1-2 gün hafif egzersiz",
                "Haftada 3-5 gün orta egzersiz",
                "Haftada 6-7 gün yoğun egzersiz",
                "Günde 2 antrenman, fiziksel iş"
            )))
        spinnerOgun.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
            listOf("2 öğün", "3 öğün", "4 öğün", "5 öğün")))
        spinnerSakatlik.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
            listOf("Yok", "Diz", "Omuz", "Bel", "Kalça")))

        spinnerHedef.setText("Güç ve Kondisyon", false)
        spinnerEkipman.setText("Full Gym", false)
        spinnerAktivite.setText("Haftada 3-5 gün orta egzersiz", false)
        spinnerOgun.setText("3 öğün", false)
        spinnerSakatlik.setText("Yok", false)

        val siddetLabels = listOf("0 (Yok)", "1 (Hafif)", "2 (Orta)", "3 (Ağır)")
        seekSakatlik.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSakatlikSiddet.text = "Seçilen: ${siddetLabels[progress]}"
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })

        btnAnalizEt.setOnClickListener {
            val yasStr  = etYas.text.toString().trim()
            val boyStr  = etBoy.text.toString().trim()
            val kiloStr = etKilo.text.toString().trim()

            if (yasStr.isEmpty() || boyStr.isEmpty() || kiloStr.isEmpty()) {
                Toast.makeText(this, "Lütfen yaş, boy ve kilo alanlarını doldurun.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Seçili günleri topla
            val seciliGunler = mutableListOf<String>()
            if (cbPazartesi.isChecked) seciliGunler.add("Pazartesi")
            if (cbSali.isChecked) seciliGunler.add("Sali")
            if (cbCarsamba.isChecked) seciliGunler.add("Carsamba")
            if (cbPersembe.isChecked) seciliGunler.add("Persembe")
            if (cbCuma.isChecked) seciliGunler.add("Cuma")
            if (cbCumartesi.isChecked) seciliGunler.add("Cumartesi")
            if (cbPazar.isChecked) seciliGunler.add("Pazar")

            if (seciliGunler.isEmpty()) {
                Toast.makeText(this, "Lütfen en az bir antrenman günü seçin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val yas  = yasStr.toInt()
            val boy  = boyStr.toInt()
            val kilo = kiloStr.toDouble()
            val sporGecmisi = etSporGecmisi.text.toString().trim().toIntOrNull() ?: 0
            val cinsiyetStr = if (rgCinsiyet.checkedRadioButtonId == R.id.rbErkek) "Erkek" else "Kadın"

            val aktiviteVal = when (spinnerAktivite.text.toString()) {
                "Masa başı iş, egzersiz yok"          -> 1.2
                "Haftada 1-2 gün hafif egzersiz"       -> 1.375
                "Haftada 3-5 gün orta egzersiz"        -> 1.55
                "Haftada 6-7 gün yoğun egzersiz"       -> 1.725
                "Günde 2 antrenman, fiziksel iş"       -> 1.9
                else                                   -> 1.55
            }

            val ogunVal        = spinnerOgun.text.toString().filter { it.isDigit() }.toIntOrNull() ?: 3
            val sakatlikBolge  = spinnerSakatlik.text.toString()
            val sakatlikSiddet = seekSakatlik.progress.toDouble()
            val vki = Math.round(kilo / ((boy / 100.0) * (boy / 100.0)) * 100) / 100.0

            val json = JSONObject().apply {
                put("Yas", yas)
                put("Cinsiyet", cinsiyetStr)
                put("Boy", boy)
                put("Kilo", kilo)
                put("Aktivite_Seviyesi", aktiviteVal)
                put("Sakatlik_Bolgesi", sakatlikBolge)
                put("Sakatlik_Siddeti", sakatlikSiddet)
                put("Spor_Gecmisi_Yil", sporGecmisi)
                put("Hedef", spinnerHedef.text.toString())
                put("Ekipman", spinnerEkipman.text.toString())
                put("Ogun_Tercihi", ogunVal)
                put("VKI", vki)
            }

            btnAnalizEt.isEnabled = false
            btnAnalizEt.text = "Analiz ediliyor..."

            val body    = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(API_URL).post(body).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        btnAnalizEt.isEnabled = true
                        btnAnalizEt.text = "Programımı Oluştur"
                        Toast.makeText(this@MainActivity,
                            "Sunucuya bağlanılamadı. Flask API çalışıyor mu?", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseStr = response.body?.string() ?: ""
                    runOnUiThread {
                        btnAnalizEt.isEnabled = true
                        btnAnalizEt.text = "Programımı Oluştur"
                        try {
                            val result = JSONObject(responseStr)
                            if (result.getString("status") == "success") {
                                val program      = result.getString("program_etiketi")
                                val kalori       = result.getInt("kalori")
                                val detaylar     = result.getJSONObject("detaylar")
                                val antrenmanAdi = detaylar.optString("Antrenman_Adi", program)

                                val intent = Intent(this@MainActivity, ResultActivity::class.java).apply {
                                    putExtra("program_adi", antrenmanAdi)
                                    putExtra("kalori", kalori)
                                    putExtra("etiket", program)
                                    putExtra("detaylar", detaylar.toString())
                                    putExtra("secili_gunler", seciliGunler.joinToString(","))
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@MainActivity,
                                    "Hata: ${result.optString("message")}", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@MainActivity,
                                "Yanıt işlenemedi: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
        }
    }
}
