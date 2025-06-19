package com.example.projectakhir.data;

import android.util.Log;
import com.example.projectakhir.data.firebase.RealtimeDbSource;
import com.example.projectakhir.data.model.Review;
import java.util.ArrayList;
import java.util.List;

public class ReviewDataSeeder {
    private static final String TAG = "ReviewDataSeeder";

    public static void seedReviews() {
        RealtimeDbSource realtimeDbSource = new RealtimeDbSource();
        List<Review> reviews = createDummyReviews();

        for (Review review : reviews) {
            realtimeDbSource.addReview(review);
        }
    }

    private static List<Review> createDummyReviews() {
        List<Review> reviews = new ArrayList<>();

        // Review untuk prod_001 (Royal Canin Kitten Food)
        reviews.add(createReview(
            "review_001",
            "prod_001",
            "user123",
            4.5f,
            "Makanan kucing yang sangat bagus! Anak kucing saya suka sekali. Pengiriman cepat dan produk original. Akan beli lagi nanti.",
            1703123456789L,
            "https://firebasestorage.googleapis.com/v0/b/pawpal0.appspot.com/o/review_images%2Freview_001_1703123456789.jpg?alt=media"
        ));

        reviews.add(createReview(
            "review_002",
            "prod_001",
            "user456",
            5.0f,
            "Royal Canin memang premium! Kucing saya jadi lebih sehat dan bulunya mengkilap. Worth it banget untuk investasi kesehatan kucing.",
            1703123456788L,
            "https://firebasestorage.googleapis.com/v0/b/pawpal0.appspot.com/o/review_images%2Freview_002_1703123456788.jpg?alt=media"
        ));

        // Review untuk prod_002 (Pedigree Adult Dog Food)
        reviews.add(createReview(
            "review_003",
            "prod_002",
            "user789",
            4.0f,
            "Pedigree untuk anjing dewasa sangat bagus. Anjing saya suka dan tidak ada masalah pencernaan. Harga terjangkau untuk kualitas bagus.",
            1703123456787L,
            null
        ));

        reviews.add(createReview(
            "review_004",
            "prod_002",
            "user101",
            4.2f,
            "Makanan yang bagus untuk anjing saya. Nutrisi lengkap dan anjing jadi lebih aktif. Pengiriman cepat dan packing aman.",
            1703123456786L,
            "https://firebasestorage.googleapis.com/v0/b/pawpal0.appspot.com/o/review_images%2Freview_004_1703123456786.jpg?alt=media"
        ));

        // Review untuk prod_003 (Cat Litter Box Premium)
        reviews.add(createReview(
            "review_005",
            "prod_003",
            "user202",
            4.7f,
            "Litter box premium yang sangat bagus! Filter bau bekerja dengan baik dan tutupnya rapat. Kucing saya nyaman menggunakannya.",
            1703123456785L,
            "https://firebasestorage.googleapis.com/v0/b/pawpal0.appspot.com/o/review_images%2Freview_005_1703123456785.jpg?alt=media"
        ));

        // Review untuk prod_004 (Dog Collar Leather)
        reviews.add(createReview(
            "review_006",
            "prod_004",
            "user303",
            4.3f,
            "Kalung kulit yang berkualitas tinggi. Gesper stainless steel tidak mudah rusak dan ukurannya pas untuk anjing saya.",
            1703123456784L,
            null
        ));

        // Review untuk prod_005 (Cat Scratching Post)
        reviews.add(createReview(
            "review_007",
            "prod_005",
            "user404",
            4.6f,
            "Tiang garukan yang sangat bagus! Kucing saya suka sekali dan tidak lagi menggaruk sofa. Ada tempat tidur di atasnya juga.",
            1703123456783L,
            "https://firebasestorage.googleapis.com/v0/b/pawpal0.appspot.com/o/review_images%2Freview_007_1703123456783.jpg?alt=media"
        ));

        // Review untuk prod_006 (Whiskas Cat Treats)
        reviews.add(createReview(
            "review_008",
            "prod_006",
            "user505",
            4.4f,
            "Snack Whiskas yang enak! Kucing saya langsung suka dengan rasa tuna dan salmon. Bagus untuk treat harian.",
            1703123456782L,
            "https://firebasestorage.googleapis.com/v0/b/pawpal0.appspot.com/o/review_images%2Freview_008_1703123456782.jpg?alt=media"
        ));

        // Review untuk prod_007 (Dog Shampoo Anti Flea)
        reviews.add(createReview(
            "review_009",
            "prod_007",
            "user606",
            4.1f,
            "Shampoo anti kutu yang efektif. Anjing saya jadi bersih dan tidak ada kutu lagi. Formula lembut tidak membuat kulit kering.",
            1703123456781L,
            null
        ));

        // Review untuk prod_008 (Cat Carrier Travel)
        reviews.add(createReview(
            "review_010",
            "prod_008",
            "user707",
            4.8f,
            "Carrier kucing yang sangat bagus! Ventilasi baik dan kucing tidak stres saat perjalanan. Ukuran pas untuk kucing dewasa.",
            1703123456780L,
            "https://firebasestorage.googleapis.com/v0/b/pawpal0.appspot.com/o/review_images%2Freview_010_1703123456780.jpg?alt=media"
        ));

        // Review untuk prod_009 (Dog Bed Memory Foam)
        reviews.add(createReview(
            "review_011",
            "prod_009",
            "user808",
            4.9f,
            "Tempat tidur memory foam yang sangat nyaman! Anjing saya langsung tidur nyenyak. Kualitas premium dan tahan lama.",
            1703123456779L,
            "https://firebasestorage.googleapis.com/v0/b/pawpal0.appspot.com/o/review_images%2Freview_011_1703123456779.jpg?alt=media"
        ));

        // Review untuk prod_010 (Cat Vitamin Supplement)
        reviews.add(createReview(
            "review_012",
            "prod_010",
            "user909",
            4.0f,
            "Vitamin kucing yang bagus untuk kesehatan. Kucing saya jadi lebih aktif dan nafsu makan meningkat. Akan beli lagi.",
            1703123456778L,
            null
        ));

        return reviews;
    }

    private static Review createReview(String id, String productId, String userId, float rating, 
                                     String comment, long timestamp, String imageUrl) {
        Review review = new Review();
        review.setId(id);
        review.setProductId(productId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment);
        review.setTimestamp(timestamp);
        review.setImageUrl(imageUrl);
        return review;
    }
} 