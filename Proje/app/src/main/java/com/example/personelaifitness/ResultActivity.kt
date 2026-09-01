package com.example.personelaifitness

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import org.json.JSONObject

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val programAdi  = intent.getStringExtra("program_adi") ?: "—"
        val kalori      = intent.getIntExtra("kalori", 0)
        val etiket      = intent.getStringExtra("etiket") ?: "—"
        val detaylarStr = intent.getStringExtra("detaylar") ?: "{}"
        val seciliGunler = intent.getStringExtra("secili_gunler")?.split(",") ?: listOf("Pazartesi", "Sali", "Carsamba")

        findViewById<TextView>(R.id.tvProgramAdi).text = programAdi
        findViewById<TextView>(R.id.tvKalori).text     = "$kalori kcal"
        findViewById<TextView>(R.id.tvEtiket).text     = etiket

        val container = findViewById<LinearLayout>(R.id.gunlerContainer)
        container.removeAllViews()

        val gunIsimleri = mapOf(
            "Pazartesi" to "Pazartesi",
            "Sali" to "Salı",
            "Carsamba" to "Çarşamba",
            "Persembe" to "Perşembe",
            "Cuma" to "Cuma",
            "Cumartesi" to "Cumartesi",
            "Pazar" to "Pazar"
        )

        // Tüm program günleri (API'den gelen)
        val programGunleri = listOf("Pazartesi", "Sali", "Carsamba", "Persembe", "Cuma", "Cumartesi", "Pazar")

        try {
            val detaylar = JSONObject(detaylarStr)
            var programGunIndex = 0

            for (gun in seciliGunler) {
                // Seçili güne sıradaki program gününü ata
                val programGunu = if (programGunIndex < programGunleri.size) programGunleri[programGunIndex] else null
                val gunDetay = if (programGunu != null) detaylar.optJSONObject(programGunu) else null
                programGunIndex++

                // Kart oluştur
                val card = CardView(this).apply {
                    radius = 24f
                    cardElevation = 4f
                    setCardBackgroundColor(0xFFFFFFFF.toInt())
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(0, 0, 0, 24)
                    layoutParams = params
                }

                val inner = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(40, 36, 40, 36)
                }

                // Gün başlığı
                inner.addView(TextView(this).apply {
                    text = gunIsimleri[gun] ?: gun
                    textSize = 15f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setTextColor(0xFF1A1A1A.toInt())
                    setPadding(0, 0, 0, 16)
                })

                if (gunDetay != null) {
                    val odak = gunDetay.optString("Odak", "")
                    val hareketlerArr = gunDetay.optJSONArray("Hareketler")
                    val beslenme = gunDetay.optString("Beslenme_Tipi", "")

                    inner.addView(TextView(this).apply {
                        text = "🎯 $odak"
                        textSize = 13f
                        setTextColor(0xFF555555.toInt())
                        setPadding(0, 0, 0, 8)
                    })

                    if (hareketlerArr != null) {
                        val sb = StringBuilder()
                        for (i in 0 until hareketlerArr.length()) {
                            sb.append("• ${hareketlerArr.getString(i)}\n")
                        }
                        inner.addView(TextView(this).apply {
                            text = sb.toString().trimEnd()
                            textSize = 13f
                            setTextColor(0xFF333333.toInt())
                            setPadding(0, 0, 0, 8)
                        })
                    }

                    inner.addView(TextView(this).apply {
                        text = "🥗 Beslenme: $beslenme"
                        textSize = 12f
                        setTextColor(0xFF4CAF50.toInt())
                    })
                } else {
                    inner.addView(TextView(this).apply {
                        text = "💤 Dinlenme günü"
                        textSize = 13f
                        setTextColor(0xFF888888.toInt())
                    })
                }

                card.addView(inner)
                container.addView(card)
            }
        } catch (e: Exception) {
            // hata sessizce geç
        }

        findViewById<MaterialButton>(R.id.btnGeriDon).setOnClickListener {
            finish()
        }
    }
}
