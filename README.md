# PersonelAI Fitness — Yapay Zeka Destekli Kişiselleştirilmiş Antrenman Programı Öneri Sistemi

## 📋 Proje Özeti

PersonelAI Fitness, kullanıcının yaş, boy, kilo, cinsiyet, sakatlık durumu, antrenman hedefi ve ekipman bilgilerini analiz ederek yapay zeka ile kişiye özel antrenman programı ve günlük kalori ihtiyacı öneren bir mobil uygulamadır.

Sistem üç katmanlı bir mimari üzerine kurulmuştur:

- **Android Uygulaması (Kotlin):** Kullanıcıdan veri toplayan ve sonucu gösteren arayüz katmanı.
- **Flask REST API (Python):** Gelen verileri işleyip yapay zeka modelini çalıştıran sunucu katmanı.
- **Random Forest Modeli (scikit-learn):** Kullanıcı profiline en uygun antrenman programını tahmin eden makine öğrenmesi katmanı.

Model, beş farklı program etiketi (`PPL_SPLIT`, `GUC_5X5`, `REHABILITASYON`, `YAG_YAKIMI`, `KILO_ALMA`) arasından kullanıcıya en uygun olanı sınıflandırmaktadır. Test veri setinde %100 doğruluk elde edilmiştir. Günlük kalori ihtiyacı Harris-Benedict BMR formülü ile hesaplanmaktadır.

## 📁 Klasör Yapısı

```
Kod/
├── Proje/                         → Android Studio projesi (Kotlin)
│   └── app/src/main/java/.../personelaifitness/
│       ├── SplashActivity.kt      → Açılış ekranı
│       ├── MainActivity.kt        → Veri giriş ekranı
│       └── ResultActivity.kt      → Sonuç ekranı
├── Antrenman_Sistemi_API/         → Flask API ve yapay zeka modeli
│   ├── main_api.ipynb             → Flask API sunucu kodu
│   ├── model_egitici.ipynb        → Random Forest model eğitim kodu
│   ├── personel_ai_model.pkl      → Eğitilmiş model
│   ├── feature_encoders.pkl       → Özellik kodlayıcılar
│   ├── target_encoder.pkl         → Etiket kodlayıcı
│   └── program_icerikleri.json    → Antrenman/beslenme bilgi bankası
└── FitnessAI.sql                  → Veritabanı şeması ve örnek veriler
```

## 🛠️ Gerekli Kütüphaneler ve Kurulum

### Backend (Python)

Python 3.10+ ve Anaconda/Jupyter Notebook önerilir.

```
pip install flask pandas scikit-learn joblib
```

### Android Uygulaması

- Android Studio (Ladybug veya üzeri)
- JDK 11+
- Android SDK 35 (minSdk 24)

Gerekli kütüphaneler `app/build.gradle.kts` içinde tanımlıdır ve Gradle Sync sırasında otomatik indirilir:

- OkHttp (HTTP istemcisi)
- Material Components
- CardView

### Veritabanı (Opsiyonel)

- Microsoft SQL Server / SQL Server Management Studio
- `FitnessAI.sql` script dosyası ile veritabanı şeması kurulabilir.

## ▶️ Adım Adım Çalıştırma Talimatı

### 1. Flask API'yi Başlatma

1. `Antrenman_Sistemi_API` klasörünü Jupyter Notebook ile aç.
2. `main_api.ipynb` dosyasını aç.
3. Tüm hücreleri sırayla çalıştır (Kernel → Restart & Run All).
4. Terminalde şu çıktı görülmelidir: `Running on http://0.0.0.0:5000`
5. API artık `http://localhost:5000/asistan` adresinde POST isteklerini dinlemektedir.

### 2. Android Uygulamasını Çalıştırma

1. Android Studio'yu aç → File → Open → Proje klasörünü seç.
2. Gradle Sync işleminin tamamlanmasını bekle.
3. `MainActivity.kt` içindeki `API_URL` değişkenini kontrol et:
   - Emülatör kullanılıyorsa: `http://10.0.2.2:5000/asistan` (varsayılan, değişiklik gerekmez)
   - Gerçek cihaz kullanılıyorsa: Bilgisayarın yerel ağ IP adresi ile değiştirilmelidir, örn. `http://192.168.1.X:5000/asistan`
4. Üst menüden bir emülatör seç veya gerçek cihazı USB ile bağla.
5. Run (▶) butonuna bas.
6. Uygulama açılış ekranından sonra veri giriş formuna yönlenecektir.

### 3. Uygulamayı Test Etme

1. Formdaki yaş, boy, kilo gibi alanları doldur.
2. Antrenman yapılacak günleri seç.
3. Hedef, ekipman, aktivite seviyesi ve sakatlık bilgilerini belirle.
4. "Programımı Oluştur" butonuna bas.
5. Flask API çalışıyor olduğu sürece, birkaç saniye içinde kişiselleştirilmiş antrenman programı ve günlük kalori ihtiyacı sonuç ekranında görüntülenecektir.

## ⚠️ Önemli Notlar

- Flask API ve Android uygulaması aynı ağda çalışmalıdır (emülatör için bu otomatik sağlanır).
- API her çalıştırıldığında `personel_ai_model.pkl`, `feature_encoders.pkl` ve `target_encoder.pkl` dosyalarının `Antrenman_Sistemi_API` klasöründe bulunması gerekmektedir.
- Modeli yeniden eğitmek için `model_egitici.ipynb` dosyası, ham veri setleri ile birlikte kullanılabilir.

## 👤 Hazırlayan

Kutay Kubilay Kayhan — İstanbul Topkapı Üniversitesi, Mühendislik ve Doğa Bilimleri Fakültesi, Yazılım Mühendisliği Bitirme Projesi
