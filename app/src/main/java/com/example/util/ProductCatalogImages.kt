package com.example.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary

data class CatalogProductItem(
  val id: String,
  val name: String,
  val category: String,
  val imageUrl: String,
  val defaultUnit: String = "كيلو",
  val defaultDescription: String = "",
  val suggestedPrice: Double = 0.0
)

object ProductCatalogLibrary {
  val categories = listOf(
    "الكل",
    "الخضروات",
    "الفواكه",
    "الفراخ والطيور",
    "اللحوم والمجمدات",
    "السجق والهوت دوج",
    "الأسماك والبحريات",
    "البقوليات والحبوب",
    "الألبان والبقالة"
  )

  val items: List<CatalogProductItem> = listOf(
    // 1. الخضروات
    CatalogProductItem("veg_tomato", "طماطم بلدي فرز أول", "الخضروات", "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=600&auto=format&fit=crop&q=80", "كيلو", "طماطم طازجة حمراء فرز أول للسلطة والطبخ", 15.0),
    CatalogProductItem("veg_cucumber", "خيار بلدي طازج", "الخضروات", "https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=600&auto=format&fit=crop&q=80", "كيلo", "خيار طازج ومقرمش للسلطات", 18.0),
    CatalogProductItem("veg_potato", "بطاطس تحمير وطبخ", "الخضروات", "https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=600&auto=format&fit=crop&q=80", "كيلو", "بطاطس سبونتا ممتازة للتحمير وللطبخ", 20.0),
    CatalogProductItem("veg_onion_red", "بصل أحمر بلدي", "الخضروات", "https://images.unsplash.com/photo-1618512496248-a07fe83aa8cb?w=600&auto=format&fit=crop&q=80", "كيلو", "بصل أحمر غني بالنكهة ممتاز للتخزين", 22.0),
    CatalogProductItem("veg_onion_white", "بصل أبيض ذهبي", "الخضروات", "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=600&auto=format&fit=crop&q=80", "كيلو", "بصل أبيض خفيف ومثالي للشوربات والطهي", 20.0),
    CatalogProductItem("veg_garlic", "ثوم بلدي فصوص", "الخضروات", "https://images.unsplash.com/photo-1588691894565-38501254bf56?w=600&auto=format&fit=crop&q=80", "نصف كيلو", "ثوم مصري بلدي ذو رائحة ونكهة قوية", 35.0),
    CatalogProductItem("veg_pepper_green", "فلفل رومي أخضر", "الخضروات", "https://images.unsplash.com/photo-1563565375-f3fdfdbefa83?w=600&auto=format&fit=crop&q=80", "كيلو", "فلفل رومي طازج وغني بفيتامين سي", 20.0),
    CatalogProductItem("veg_pepper_color", "فلفل ألوان أحمر وأصفر", "الخضروات", "https://images.unsplash.com/photo-1526470608268-f674ce90ebd4?w=600&auto=format&fit=crop&q=80", "كيلو", "فلفل ألوان حلو وطازج للسلطة والشوي", 40.0),
    CatalogProductItem("veg_pepper_hot", "فلفل حار شطة", "الخضروات", "https://images.unsplash.com/photo-1588252303782-cb80119abd6d?w=600&auto=format&fit=crop&q=80", "ربع كيلو", "فلفل حار بلدي للشطة والمخللات", 12.0),
    CatalogProductItem("veg_eggplant_black", "باذنجان رومي أسود", "الخضروات", "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&auto=format&fit=crop&q=80", "كيلو", "باذنجان رومي لامع وخفيف للمسقعة والقلي", 15.0),
    CatalogProductItem("veg_eggplant_arus", "باذنجان عروس أبيض وأسود", "الخضروات", "https://images.unsplash.com/photo-1590779033100-9f60a05a013d?w=600&auto=format&fit=crop&q=80", "كيلو", "باذنجان عروس طازج للمحشي والمخلل", 16.0),
    CatalogProductItem("veg_zucchini", "كوسة طازجة", "الخضروات", "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=600&auto=format&fit=crop&q=80", "كيلو", "كوسة طرية وصغيرة الحجم للمحشي والطبخ", 20.0),
    CatalogProductItem("veg_carrot", "جزر سكري طازج", "الخضروات", "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=600&auto=format&fit=crop&q=80", "كيلو", "جزر سكري بلدي طازج للسلطات والعصير", 18.0),
    CatalogProductItem("veg_lemon", "ليمون بلدي بنزهير", "الخضروات", "https://images.unsplash.com/photo-1533082619047-9750d53c5f2b?w=600&auto=format&fit=crop&q=80", "نصف كيلو", "ليمون بلدي حامض غني بالعصير الطبيعي", 25.0),
    CatalogProductItem("veg_molokhia", "ملوخية خضراء فلاحي", "الخضروات", "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=600&auto=format&fit=crop&q=80", "حزمة / كيلو", "ملوخية خضراء طازجة مقطوفة يومياً", 15.0),
    CatalogProductItem("veg_spinach", "سبانخ خضراء بلدية", "الخضروات", "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=600&auto=format&fit=crop&q=80", "كيلو", "سبانخ طازجة غنية بالحديد والمعادن", 16.0),
    CatalogProductItem("veg_lettuce", "خس كابوتشا طازج", "الخضروات", "https://images.unsplash.com/photo-1556801712-76c8eb07bbc9?w=600&auto=format&fit=crop&q=80", "واحدة", "خس كابوتشا مقرمش وطازج للسلطات والسندوتشات", 12.0),
    CatalogProductItem("veg_herbs", "شبت وبقدونس وكزبرة", "الخضروات", "https://images.unsplash.com/photo-1608686207856-001b95cf60ca?w=600&auto=format&fit=crop&q=80", "ربطة مشكلة", "خضرة محشي وسلطة طازجة وخضراء", 8.0),
    CatalogProductItem("veg_cauliflower", "قرنبيط أبيض طازج", "الخضروات", "https://images.unsplash.com/photo-1568584711075-3d021a7c3ca3?w=600&auto=format&fit=crop&q=80", "رأس واحدة", "قرنبيط طازج ومتماسك للقلي والطهي", 25.0),
    CatalogProductItem("veg_cabbage", "كرنب بلدي للمحشي", "الخضروات", "https://images.unsplash.com/photo-1594282486552-05b4d80fbb9f?w=600&auto=format&fit=crop&q=80", "واحدة", "كرنب بلدي مسكر ومورق للمحشي الشهي", 30.0),
    CatalogProductItem("veg_okra", "بامية خضراء بلدي", "الخضروات", "https://images.unsplash.com/photo-1425543103986-22abb7d7e8d2?w=600&auto=format&fit=crop&q=80", "كيلو", "بامية قمع خضراء صغيرة للطواجن والويكا", 45.0),

    // 2. الفواكه
    CatalogProductItem("fruit_apple_red", "تفاح أحمر سكري", "الفواكه", "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=600&auto=format&fit=crop&q=80", "كيلو", "تفاح أحمر إيطالي سكري ومقرمش", 65.0),
    CatalogProductItem("fruit_apple_green", "تفاح أخضر جراني سميث", "الفواكه", "https://images.unsplash.com/photo-1619546813926-a78fa6372cd2?w=600&auto=format&fit=crop&q=80", "كيلو", "تفاح أخضر حامض حلو للدايت والانتعاش", 70.0),
    CatalogProductItem("fruit_banana", "موز بلدي فاخر", "الفواكه", "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=600&auto=format&fit=crop&q=80", "كيلو", "موز بلدي طبيعي حلو المذاق وغني بالبوتاسيوم", 25.0),
    CatalogProductItem("fruit_orange", "برتقال أبو صرة / عصير", "الفواكه", "https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=600&auto=format&fit=crop&q=80", "كيلو", "برتقال طازج مليء بالعصير الطبيعي وفيتامين سي", 20.0),
    CatalogProductItem("fruit_tangerine", "يوسفي بلدي موري", "الفواكه", "https://images.unsplash.com/photo-1557800636-894a64c1696f?w=600&auto=format&fit=crop&q=80", "كيلو", "يوسفي بلدي مسكر سهل التقشير", 22.0),
    CatalogProductItem("fruit_grapes_red", "عنب أحمر كريمسون", "الفواكه", "https://images.unsplash.com/photo-1537640538966-79f369143f8f?w=600&auto=format&fit=crop&q=80", "كيلو", "عنب أحمر بدون بذر حلو المذاق", 45.0),
    CatalogProductItem("fruit_grapes_white", "عنب أبيض بناتي", "الفواكه", "https://images.unsplash.com/photo-1596363505729-4190a9506133?w=600&auto=format&fit=crop&q=80", "كيلو", "عنب أبيض بناتي مسكر وبدون بذر", 40.0),
    CatalogProductItem("fruit_mango", "مانجو سكري / عويس", "الفواكه", "https://images.unsplash.com/photo-1553279768-865429fa0078?w=600&auto=format&fit=crop&q=80", "كيلو", "مانجو فاخرة ذات رائحة عطرة وحلاوة مميزة", 55.0),
    CatalogProductItem("fruit_strawberry", "فراولة بلدي سكرية", "الفواكه", "https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=600&auto=format&fit=crop&q=80", "كيلو", "فراولة حمراء ناضجة طازجة للحلويات والعصائر", 35.0),
    CatalogProductItem("fruit_watermelon", "بطيخ أحمر جيزة سكري", "الفواكه", "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=600&auto=format&fit=crop&q=80", "بالحبة / كجم", "بطيخ أحمر مرمل شديد الحلاوة", 60.0),
    CatalogProductItem("fruit_cantaloupe", "شمام / كانتالوب عسلي", "الفواكه", "https://images.unsplash.com/photo-1571575179703-4bde44fb1417?w=600&auto=format&fit=crop&q=80", "كيلو", "كانتالوب مسكر وريان ومنعش", 25.0),
    CatalogProductItem("fruit_peach", "خوخ سكري سيناوي", "الفواكه", "https://images.unsplash.com/photo-1629828874514-c1e5103f2150?w=600&auto=format&fit=crop&q=80", "كيلو", "خوخ طازج ممتلئ بالعصارة السكرية", 45.0),
    CatalogProductItem("fruit_guava", "جوافة بناتية فلاحي", "الفواكه", "https://images.unsplash.com/photo-1536511136894-39fa8fa44040?w=600&auto=format&fit=crop&q=80", "كيلو", "جوافة بيضاء سكرية برائحة فواحة مميزة", 30.0),
    CatalogProductItem("fruit_pomegranate", "رمان أسيوطي منفلوطي", "الفواكه", "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&auto=format&fit=crop&q=80", "كيلو", "رمان أحمر ياقوتي سكري حبوبه ممتلئة", 35.0),
    CatalogProductItem("fruit_dates", "بلح رطب أمهات وسيوى", "الفواكه", "https://images.unsplash.com/photo-1594282486552-05b4d80fbb9f?w=600&auto=format&fit=crop&q=80", "علبة / كجم", "تمر وبلح طبيعي مغذي وغني بالطاقة", 40.0),

    // 3. الفراخ والطيور
    CatalogProductItem("poultry_whole_chicken", "دجاج بلدي كامل طازج", "الفراخ والطيور", "https://images.unsplash.com/photo-1587593810167-a84920ea0781?w=600&auto=format&fit=crop&q=80", "كيلو جرام", "دجاج بلدي مذبوح ومنظف طازج يومياً", 130.0),
    CatalogProductItem("poultry_white_chicken", "دجاج أبيض مزارع طازج", "الفراخ والطيور", "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=600&auto=format&fit=crop&q=80", "كيلو جرام", "دجاج أبيض طازج لحم طري وصحي", 95.0),
    CatalogProductItem("poultry_panee", "صدور بانيه دجاج مخلية", "الفراخ والطيور", "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=600&auto=format&fit=crop&q=80", "كيلو", "صدور دجاج مخلية ومقطعة شرائح بانيه جاهزة", 210.0),
    CatalogProductItem("poultry_thighs", "وراك دجاج طازجة", "الفراخ والطيور", "https://images.unsplash.com/photo-1587593810167-a84920ea0781?w=600&auto=format&fit=crop&q=80", "كيلو", "وراك دجاج طازجة ومنظفة للطهي والشوي", 105.0),
    CatalogProductItem("poultry_drumsticks", "دبابيس دجاج طازجة", "الفراخ والطيور", "https://images.unsplash.com/photo-1587593810167-a84920ea0781?w=600&auto=format&fit=crop&q=80", "كيلو", "دبابيس دجاج ممتازة للقلي والبروستد والفرن", 125.0),
    CatalogProductItem("poultry_shish", "شيش طاووق صدور وأوراك", "الفراخ والطيور", "https://images.unsplash.com/photo-1603360946369-dc9bb6258143?w=600&auto=format&fit=crop&q=80", "كيلو", "مكعبات دجاج شيش طاووق مخلية جاهزة للتتبيل", 200.0),
    CatalogProductItem("poultry_giblets", "كبد وقوانص دجاج طازجة", "الفراخ والطيور", "https://images.unsplash.com/photo-1587593810167-a84920ea0781?w=600&auto=format&fit=crop&q=80", "كيلو", "كبد وقوانص طازجة ونظيفة للأرز والخلطة", 110.0),
    CatalogProductItem("poultry_frozen_whole", "دجاج مجمد كامل درجة أولى", "الفراخ والطيور", "https://images.unsplash.com/photo-1587593810167-a84920ea0781?w=600&auto=format&fit=crop&q=80", "كيلو جرام", "دجاج مجمد مذبوح على الشريعة الإسلامية", 115.0),

    // 4. اللحوم واللحوم المجمدة
    CatalogProductItem("meat_minced_fresh", "لحم بقري مفروم بلدي", "اللحوم والمجمدات", "https://images.unsplash.com/photo-1588168333986-5078d3ae3976?w=600&auto=format&fit=crop&q=80", "كيلو", "لحم بقري مفروم أحمر طازج قليل الدهن", 380.0),
    CatalogProductItem("meat_cubes_fresh", "لحم بقري مكعبات خضار", "اللحوم والمجمدات", "https://images.unsplash.com/photo-1603048588665-791ca8aea617?w=600&auto=format&fit=crop&q=80", "كيلو", "لحم كندوز أحمر مكعبات طازج للطواجن والشوربة", 400.0),
    CatalogProductItem("meat_kofta_fresh", "كفتة حاتي متبلة بلدي", "اللحوم والمجمدات", "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=600&auto=format&fit=crop&q=80", "كيلو", "كفتة مشوية متبلة بخلطة بهارات الحاتي الأصلية", 360.0),
    CatalogProductItem("meat_liver_fresh", "كبدة بلدي طازجة", "اللحوم والمجمدات", "https://images.unsplash.com/photo-1603048588665-791ca8aea617?w=600&auto=format&fit=crop&q=80", "نصف كيلو", "كبدة بتلو وبلدي طازجة ممتازة للإسكندراني", 220.0),
    CatalogProductItem("meat_minced_frozen", "لحم مفروم برازيلي مجمد ممتاز", "اللحوم والمجمدات", "https://images.unsplash.com/photo-1588168333986-5078d3ae3976?w=600&auto=format&fit=crop&q=80", "كيلو", "لحم مفروم مجمد عالي الجودة للطهي والحشو", 240.0),
    CatalogProductItem("meat_cubes_frozen", "لحم كباب حلة مجمد قطع", "اللحوم والمجمدات", "https://images.unsplash.com/photo-1603048588665-791ca8aea617?w=600&auto=format&fit=crop&q=80", "كيلو", "قطع لحم مجمد منتقاة بعناية لكباب الحلة", 260.0),
    CatalogProductItem("meat_liver_frozen", "كبدة مجمدة شرائح إسكندراني", "اللحوم والمجمدات", "https://images.unsplash.com/photo-1603048588665-791ca8aea617?w=600&auto=format&fit=crop&q=80", "كيلو", "شرائح كبدة مجمدة ممتازة للتتبيل والقلي السريع", 140.0),
    CatalogProductItem("meat_burger_frozen", "برجر لحم بقري جامبو", "اللحوم والمجمدات", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&auto=format&fit=crop&q=80", "علبة 8 قطع", "برجر بقري صافي جاهز للشوي السريع", 160.0),

    // 5. السجق والهوت دوج
    CatalogProductItem("sausage_baladi", "سجق بلدي شرقي متبل", "السجق والهوت دوج", "https://images.unsplash.com/photo-1585325701956-60dd9c8553bc?w=600&auto=format&fit=crop&q=80", "كيلو", "سجق بلدي خلطة توابل شرقية أصلية مع الفلفل والثوم", 340.0),
    CatalogProductItem("sausage_alex", "سجق إسكندراني حار", "السجق والهوت دوج", "https://images.unsplash.com/photo-1585325701956-60dd9c8553bc?w=600&auto=format&fit=crop&q=80", "كيلو", "سجق متبل على الطريقة الإسكندرانية مع صوص الطماطم", 330.0),
    CatalogProductItem("sausage_hotdog", "هوت دوج / فرانكفورتر بقري", "السجق والهوت دوج", "https://images.unsplash.com/photo-1619740455993-9e612b1af08a?w=600&auto=format&fit=crop&q=80", "عبوة 500 جرام", "أصابع هوت دوج بقري ممتاز للسندوتشات والمعجنات", 95.0),
    CatalogProductItem("sausage_smoked", "سجق مدخن فاخر", "السجق والهوت دوج", "https://images.unsplash.com/photo-1585325701956-60dd9c8553bc?w=600&auto=format&fit=crop&q=80", "نصف كيلو", "سجق مدخن برائحة الحطب الطبيعي", 170.0),
    CatalogProductItem("sausage_grill", "سجق جريل للشوي والتحمير", "السجق والهوت دوج", "https://images.unsplash.com/photo-1585325701956-60dd9c8553bc?w=600&auto=format&fit=crop&q=80", "كيلو", "أصابع سجق متماسكة وغنية باللحم للشوي على الفحم", 320.0),

    // 6. الأسماك والبحريات
    CatalogProductItem("fish_tilapia", "سمك بلطي نيلي طازج", "الأسماك والبحريات", "https://images.unsplash.com/photo-1534939561126-855b8675edd7?w=600&auto=format&fit=crop&q=80", "كيلو", "سمك بلطي حي وطازج ممتاز للقلي والشوي بالردة", 90.0),
    CatalogProductItem("fish_mullet", "سمك بوري طازج زيت وليمون", "الأسماك والبحريات", "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=600&auto=format&fit=crop&q=80", "كيلو", "سمك بوري مبطرخ ممتاز للشوي سنجاري وبالزيت والليمون", 160.0),
    CatalogProductItem("fish_shrimp_medium", "جمبري مقشر وسط طازج", "الأسماك والبحريات", "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=600&auto=format&fit=crop&q=80", "كيلو", "جمبري بحري مقشر ومنظف لطواجن السي فود والمكرونة", 320.0),
    CatalogProductItem("fish_shrimp_jumbo", "جمبري جامبو بقشره للشوي", "الأسماك والبحريات", "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=600&auto=format&fit=crop&q=80", "كيلو", "جمبري جامبو فاخر للشوي على الجريل وبتر فلاي", 480.0),
    CatalogProductItem("fish_fillet", "فيليه قشر بياض طازج", "الأسماك والبحريات", "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=600&auto=format&fit=crop&q=80", "كيلو", "لحم فيليه أبيض بدون شوك ممتاز للقلي السريع", 280.0),
    CatalogProductItem("fish_mackerel", "سمك ماكريل إسباني للشوي", "الأسماك والبحريات", "https://images.unsplash.com/photo-1534939561126-855b8675edd7?w=600&auto=format&fit=crop&q=80", "كيلو", "ماكريل غني بزيت الأوميجا 3 المفيد للصحة", 140.0),
    CatalogProductItem("fish_salmon", "ستيك سالمون نرويجي شرائح", "الأسماك والبحريات", "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=600&auto=format&fit=crop&q=80", "نصف كيلو", "شرائح سالمون وردية طازجة غنية بالدهون الصحية", 380.0),
    CatalogProductItem("fish_calamari", "كاليماري وسبيط طازج", "الأسماك والبحريات", "https://images.unsplash.com/photo-1534939561126-855b8675edd7?w=600&auto=format&fit=crop&q=80", "كيلو", "سبيط وكاليماري منظف حلقات للطواجن والقلي", 300.0),

    // 7. البقوليات والحبوب
    CatalogProductItem("grain_foul", "فول تدميس بلدي درجة أولى", "البقوليات والحبوب", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600&auto=format&fit=crop&q=80", "كيلو", "فول تدميس حبة عريضة يستوي زبدة بسرعة", 45.0),
    CatalogProductItem("grain_lentils_yellow", "عدس أصفر مصري فاخر", "البقوليات والحبوب", "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&auto=format&fit=crop&q=80", "كيلو", "عدس أصفر منقى للشوربات والفتة", 60.0),
    CatalogProductItem("grain_lentils_brown", "عدس بجبة بني للكشري", "البقوليات والحبوب", "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&auto=format&fit=crop&q=80", "كيلو", "عدس بني حبة كاملة للكشري الأصيل", 55.0),
    CatalogProductItem("grain_white_beans", "فاصوليا بيضاء جافة", "البقوليات والحبوب", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600&auto=format&fit=crop&q=80", "كيلو", "فاصوليا بيضاء ناصعة حبة ممتازة للطبيخ بالصلصة", 75.0),
    CatalogProductItem("grain_black_eyed_peas", "لوبيا ذات عين سوداء", "البقوليات والحبوب", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600&auto=format&fit=crop&q=80", "كيلو", "لوبيا بلدي منقاة ومجففة بعناية", 80.0),
    CatalogProductItem("grain_chickpeas", "حمص الشام حبة كبيرة", "البقوليات والحبوب", "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&auto=format&fit=crop&q=80", "كيلو", "حمص الشام للمشروب الساخن وللكشري والطحينة", 70.0),
    CatalogProductItem("grain_popcorn", "ذرة فيشار صفراء نقية", "البقوليات والحبوب", "https://images.unsplash.com/photo-1578849278619-e73505e9610f?w=600&auto=format&fit=crop&q=80", "نصف كيلو", "حبوب ذرة عالية التفتح والانتفاخ للفيشار الخفيف", 25.0),
    CatalogProductItem("grain_rice", "أرز مصري أبيض درجة أولى", "البقوليات والحبوب", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600&auto=format&fit=crop&q=80", "كيلو", "أرز مصري عريض الحبة حبة وحبة مفلفل", 32.0),
    CatalogProductItem("grain_pasta", "مكرونة مشكلة فاخرة", "البقوليات والحبوب", "https://images.unsplash.com/photo-1621996346565-e3d5d6281781?w=600&auto=format&fit=crop&q=80", "كيس 400 جم", "مكرونة سيمولينا نقية لا تعجن وسريعة التحضير", 16.0),
    CatalogProductItem("grain_flour", "دقيق فاخر متعدد الأغراض", "البقوليات والحبوب", "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600&auto=format&fit=crop&q=80", "كيلو", "دقيق أبيض فاخر استخراج 72% للمعجنات والخبز", 22.0),

    // 8. الألبان والبقالة
    CatalogProductItem("dairy_feta", "جبن أبيض فيتا كريمي", "الألبان والبقالة", "https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?w=600&auto=format&fit=crop&q=80", "عبوة 500 جم", "جبن فيتا ناعم قليل الملح لوجبة الإفطار", 45.0),
    CatalogProductItem("dairy_romi", "جبن رومي قديم مبشور وقطع", "الألبان والبقالة", "https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?w=600&auto=format&fit=crop&q=80", "ربع كيلو", "جبن رومي مصري معتق ذو طعم غني ومميز", 85.0),
    CatalogProductItem("dairy_milk", "حليب بقري كامل الدسم طازج", "الألبان والبقالة", "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=600&auto=format&fit=crop&q=80", "لتر", "حليب طبيعي معقم غني بالكالسيوم والقشطة", 38.0),
    CatalogProductItem("dairy_butter", "زبدة صفراء طبيعية نيوزيلندي", "الألبان والبقالة", "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=600&auto=format&fit=crop&q=80", "قالب 500 جم", "زبدة طبيعية نقية للطهي والحلويات والمخبوزات", 110.0),
    CatalogProductItem("dairy_eggs", "طبق بيض مزارع أحمر طازج", "الألبان والبقالة", "https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=600&auto=format&fit=crop&q=80", "كرتونة 30 بيضة", "بيض مزارع طازج مفحوص وكبير الحجم", 175.0),
    CatalogProductItem("grocery_oil", "زيت ذرة / عباد نقي للقلي والطبخ", "الألبان والبقالة", "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&auto=format&fit=crop&q=80", "زجاجة 800 مل", "زيت نباتي خفيف وصحي خالٍ من الكوليسترول", 65.0),
    CatalogProductItem("grocery_sugar", "سكر أبيض ناعم للتحلية", "الألبان والبقالة", "https://images.unsplash.com/photo-1587734195503-904fca47e0e9?w=600&auto=format&fit=crop&q=80", "كيلو", "سكر ناصع البياض سريع الذوبان للمشروبات والحلويات", 35.0),
    CatalogProductItem("grocery_tea", "شاي أسود حبيبات فاخر", "الألبان والبقالة", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=600&auto=format&fit=crop&q=80", "عبوة 250 جم", "شاي كيني أصيل بلون ونكهة مضبوطة", 55.0),
    CatalogProductItem("grocery_sauce", "معجون صلصة طماطم مركزة", "الألبان والبقالة", "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=600&auto=format&fit=crop&q=80", "برطمان 360 جم", "صلصة طماطم سميكة بدون مواد حافظة للطبخ", 25.0)
  )
}

@Composable
fun ProductCatalogImagePickerDialog(
  onSelectProduct: (CatalogProductItem) -> Unit,
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("الكل") }

  val filteredItems by remember(searchQuery, selectedCategory) {
    derivedStateOf {
      ProductCatalogLibrary.items.filter { item ->
        val matchesCategory = selectedCategory == "الكل" || item.category == selectedCategory
        val matchesQuery = searchQuery.isBlank() ||
          item.name.contains(searchQuery, ignoreCase = true) ||
          item.category.contains(searchQuery, ignoreCase = true) ||
          item.defaultDescription.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.90f)
        .clip(RoundedCornerShape(20.dp)),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "إغلاق")
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "مكتبة صور الأغذية الجاهزة",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = GreenDark
            )
            Text(
              text = "اختر صورة لمنتجك (خضروات، فواكه، لحوم، فراخ، أسماك، بقوليات)",
              fontSize = 11.sp,
              color = Color.Gray
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("ابحث عن صنف (طماطم، تفاح، بانيه، سجق، عدس...)") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GreenPrimary) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "مسح")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("catalog_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(ProductCatalogLibrary.categories) { cat ->
            val isSelected = cat == selectedCategory
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategory = cat },
              label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GreenPrimary,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.testTag("catalog_cat_${cat}")
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Items Grid
        if (filteredItems.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "لم يتم العثور على أصناف تطابق بحثك.\nجرّب كلمة بحث أخرى أو اختر قسماً آخر.",
              textAlign = TextAlign.Center,
              color = Color.Gray,
              fontSize = 13.sp
            )
          }
        } else {
          LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 130.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
          ) {
            items(filteredItems, key = { it.id }) { item ->
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onSelectProduct(item) }
                  .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                  .testTag("catalog_item_${item.id}")
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(95.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .background(Color.LightGray.copy(alpha = 0.2f))
                  ) {
                    AsyncImage(
                      model = item.imageUrl,
                      contentDescription = item.name,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                    // Category Tag
                    Box(
                      modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(GreenDark.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                      Text(item.category, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Text(
                    text = item.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                  )

                  Text(
                    text = "${item.suggestedPrice.toInt()} ج.م / ${item.defaultUnit}",
                    fontSize = 10.sp,
                    color = GreenPrimary,
                    fontWeight = FontWeight.SemiBold
                  )

                  Spacer(modifier = Modifier.height(4.dp))

                  Button(
                    onClick = { onSelectProduct(item) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(28.dp)
                  ) {
                    Text("اختيار الصورة", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("إلغاء والعودة")
        }
      }
    }
  }
}
