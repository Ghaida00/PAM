package com.example.projectakhir.ui.adoption;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Untuk fallback navigasi

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectakhir.R;
import com.example.projectakhir.data.Hewan; // Jika perlu mengambil data Hewan lengkap
import com.example.projectakhir.databinding.FragmentDetailHewanBinding; // Nama binding

import java.util.ArrayList;

public class DetailHewanFragment extends Fragment {

    private FragmentDetailHewanBinding binding;

    // Variabel untuk menampung data dari argumen
    private String nama;
    // Tambahkan variabel lain jika Anda mengirim lebih banyak data via argumen
    // private String kota, jenis, umur, gender, berat, deskripsi;
    // private int gambarResId;
    // private ArrayList<String> traits;

    // Data Hewan lengkap (jika diperlukan, bisa diambil dari ViewModel berdasarkan ID/nama)
    // private Hewan hewanDetail; // Anda mungkin perlu ViewModel untuk ini

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ambil argumen yang dikirim
        if (getArguments() != null) {
            try {
                // Menggunakan Safe Args
                nama = DetailHewanFragmentArgs.fromBundle(getArguments()).getNamaHewan();
                // Ambil argumen lain jika didefinisikan di nav_graph dan dikirim
                // kota = DetailHewanFragmentArgs.fromBundle(getArguments()).getKota(); // Contoh
            } catch (IllegalArgumentException e) {
                // Fallback jika Safe Args belum siap
                nama = getArguments().getString("namaHewan");
                // Ambil argumen lain secara manual
                // kota = getArguments().getString("kota"); // Contoh
                if (nama == null) {
                    handleArgumentError();
                }
            }
            // TODO: Idealnya, dapatkan detail hewan lengkap dari ViewModel/Repository
            // menggunakan 'nama' atau ID yang diterima sebagai argumen.
            // Untuk sekarang, kita mungkin perlu mengirim semua data via argumen
            // atau menggunakan data dummy sementara di sini.
            // Contoh: loadHewanDetails(nama);

        } else {
            handleArgumentError();
        }
    }

    private void handleArgumentError() {
        Toast.makeText(requireContext(), "Error: Argumen detail hewan tidak ditemukan!", Toast.LENGTH_SHORT).show();
        if (NavHostFragment.findNavController(this).getCurrentDestination() != null &&
                NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.detailHewanFragment) {
            NavHostFragment.findNavController(this).popBackStack();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailHewanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Kode dari onCreate DetailHewanActivity dipindahkan ke sini ---

        // Ambil data dari intent (SEKARANG DIAMBIL DARI ARGUMEN di onCreate)
        // Intent i = getIntent(); // Hapus
        // nama = i.getStringExtra("nama"); // Sudah diambil di onCreate
        // ... ambil data lain dari argumen atau ViewModel ...

        // --- Data Dummy Sementara (Ganti dengan data dari argumen/ViewModel) ---
        // Ini hanya contoh jika Anda belum mengirim semua data via argumen
        // Anda HARUS mengganti ini dengan data asli yang diterima
        String kota = "Surabaya (Dummy)";
        String jenis = "Kucing (Dummy)";
        String umur = "2 Thn (Dummy)";
        String gender = "Betina (Dummy)";
        String berat = "4kg (Dummy)";
        String deskripsi = "Deskripsi dummy karena data lengkap belum diambil dari argumen/ViewModel.";
        int gambarResId = R.drawable.grace_no_background; // Gambar default/dummy
        ArrayList<String> traits = new ArrayList<>();
        traits.add("Friendly");
        traits.add("Silly");
        traits.add("Playful");
        // --- Akhir Data Dummy ---


        // Setup Tombol Back (jika tidak pakai Toolbar AppActivity)
        // binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // Setup Tombol Adopt -> Navigasi ke FormAdopsiFragment
        binding.btnAdopt.setOnClickListener(v -> {
            try {
                // Pastikan action dan argumen sudah didefinisikan di nav_graph.xml
                DetailHewanFragmentDirections.ActionDetailHewanFragmentToFormAdopsiFragment action =
                        DetailHewanFragmentDirections.actionDetailHewanFragmentToFormAdopsiFragment(nama); // Kirim namaHewan
                NavHostFragment.findNavController(DetailHewanFragment.this).navigate(action);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Navigasi ke Form Adopsi belum siap.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set data tampilan menggunakan binding dan data yang sudah diambil
        binding.imgHewan.setImageResource(gambarResId);
        binding.namaHewan.setText(nama); // Nama dari argumen
        binding.kotaHewan.setText(kota); // Data dummy/argumen
        binding.detailHewan.setText(gender + "  •  " + umur + "  •  " + berat); // Data dummy/argumen
        binding.descHewan.setText(deskripsi); // Data dummy/argumen

        // Tampilkan traits secara dinamis
        setupTraitsLayout(traits); // Gunakan data dummy/argumen

        // --- Akhir kode dari onCreate DetailHewanActivity ---
    }

    // Fungsi untuk setup layout traits
    private void setupTraitsLayout(ArrayList<String> traits) {
        if (traits == null || traits.isEmpty()) return;

        binding.traitsLayout.removeAllViews(); // Hapus view lama

        if (traits.size() == 3) {
            // Tata letak khusus untuk 3 traits seperti di Activity
            binding.traitsLayout.setOrientation(LinearLayout.VERTICAL);

            // Atas (1 icon)
            LinearLayout top = new LinearLayout(requireContext());
            top.setGravity(Gravity.CENTER);
            top.addView(createTraitView(traits.get(0)));

            // Bawah (2 icon)
            LinearLayout bottom = new LinearLayout(requireContext());
            bottom.setGravity(Gravity.CENTER);
            bottom.setPadding(0, 26, 0, 0); // Jarak atas

            // Trait kiri
            LinearLayout traitLeft = createTraitView(traits.get(1));
            LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            // Sesuaikan margin antar trait bawah jika perlu
            leftParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.trait_horizontal_margin)); // Contoh pakai dimens.xml
            traitLeft.setLayoutParams(leftParams);

            // Trait kanan
            LinearLayout traitRight = createTraitView(traits.get(2));
            LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rightParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.trait_horizontal_margin)); // Contoh pakai dimens.xml
            traitRight.setLayoutParams(rightParams);


            bottom.addView(traitLeft);
            bottom.addView(traitRight);

            binding.traitsLayout.addView(top);
            binding.traitsLayout.addView(bottom);
        } else {
            // Tata letak grid biasa jika jumlah traits bukan 3
            binding.traitsLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout currentRow = null;
            final int ITEMS_PER_ROW = 3;

            for (int i = 0; i < traits.size(); i++) {
                if (i % ITEMS_PER_ROW == 0) {
                    currentRow = new LinearLayout(requireContext());
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    currentRow.setGravity(Gravity.CENTER);
                    // Tambahkan padding antar baris jika perlu
                    currentRow.setPadding(0, (i == 0 ? 0 : 8), 0, 8);
                    binding.traitsLayout.addView(currentRow);
                }

                if (currentRow != null) {
                    currentRow.addView(createTraitView(traits.get(i)));
                }
            }
        }
    }


    // Fungsi bikin satu icon trait (dipindahkan ke sini)
    private LinearLayout createTraitView(String trait) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // Atur margin antar icon trait
        lp.setMargins(16, 8, 16, 8); // Sesuaikan margin
        layout.setLayoutParams(lp);

        ImageView icon = new ImageView(requireContext());
        icon.setImageResource(getIconForTrait(trait));
        // Atur ukuran icon jika perlu (misal dari dimens.xml)
        int iconSize = getResources().getDimensionPixelSize(R.dimen.trait_icon_size); // Contoh
        icon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));

        TextView label = new TextView(requireContext());
        label.setText(capitalize(trait));
        label.setTextSize(12f); // Sesuaikan ukuran teks
        label.setTypeface(null, android.graphics.Typeface.BOLD);
        label.setGravity(Gravity.CENTER); // Pastikan teks di tengah
        label.setPadding(0, 4, 0, 0);

        layout.addView(icon);
        layout.addView(label);
        return layout;
    }

    // Icon sesuai trait (dipindahkan ke sini)
    private int getIconForTrait(String trait) {
        if (trait == null) return R.drawable.ic_paw; // Default icon
        switch (trait.toLowerCase()) {
            case "friendly": return R.drawable.ic_paw; // Ganti dengan ikon yang sesuai
            case "silly": return R.drawable.traits_cat; // Pastikan drawable ada
            case "playful": return R.drawable.traits_toy; // Pastikan drawable ada
            case "spoiled": return R.drawable.traits_spoiled; // Pastikan drawable ada
            case "lazy": return R.drawable.traits_lazy; // Pastikan drawable ada
            case "smart": return R.drawable.traits_smart; // Pastikan drawable ada
            case "fearful": return R.drawable.traits_fearful; // Pastikan drawable ada
            default: return R.drawable.ic_paw; // Default icon
        }
    }

    // Kapitalisasi huruf depan (dipindahkan ke sini)
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase(); // Konsisten lowercase setelahnya
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting
    }
}
