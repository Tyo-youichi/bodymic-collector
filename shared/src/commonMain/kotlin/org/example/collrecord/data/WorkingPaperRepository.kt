package org.example.collrecord.data

import org.example.collrecord.model.WorkingPaper

interface WorkingPaperRepository {
    suspend fun getTaskList(): List<WorkingPaper>
}

/**
 * Mock implementation — 30 data digenerate lewat kode (bukan hand-JSON, lebih gampang
 * dimaintain & nggak rawan typo). Tetap murni commonMain, nggak butuh akses platform-specific.
 *
 * Nanti kalau backend beneran siap, tinggal buat RemoteWorkingPaperRepository yang GET ke
 * ApiConfig.BASE_URL (pakai HttpClient dari createHttpClient()), tanpa perlu ubah ViewModel/UI
 * yang consume interface ini.
 */
class MockWorkingPaperRepository : WorkingPaperRepository {
    override suspend fun getTaskList(): List<WorkingPaper> = MOCK_DATA

    companion object {
        private val debiturNames = listOf(
            "Budi Santoso", "Siti Aminah", "Agus Prasetyo", "Dewi Lestari", "Rudi Hartono",
            "Ani Wijaya", "Bambang Sutrisno", "Rina Kusuma", "Hendra Gunawan", "Sri Wahyuni",
            "Joko Susilo", "Fitri Handayani", "Eko Purnomo", "Yuni Astuti", "Wahyu Nugroho",
            "Lina Marlina", "Dedi Setiawan", "Ratna Sari", "Iwan Setiadi", "Nur Aisyah",
            "Andi Firmansyah", "Melati Putri", "Tono Suherman", "Wulan Dari", "Fajar Ramadhan",
            "Indah Permata", "Slamet Riyadi", "Ayu Lestari", "Gunawan Wibowo", "Puspita Sari"
        )
        private val businessUnits = listOf("Motor", "Mobil", "Elektronik", "Kredit Multiguna")
        private val units = listOf(
            "Honda Beat 2022", "Yamaha NMAX 2021", "Toyota Avanza 2019", "Kulkas 2 Pintu",
            "Mesin Cuci Front Load", "TV LED 43 inch", "Honda Vario 2020", "Suzuki Ertiga 2018",
            "AC Split 1PK", "Kompor Gas 2 Tungku"
        )
        private val tenors = listOf(6, 12, 24, 36)
        private val addresses = listOf(
            "Jl. Merdeka No. 12, Bandung",
            "Jl. Sudirman No. 45, Jakarta Selatan",
            "Jl. Diponegoro No. 8, Semarang",
            "Jl. Gatot Subroto No. 21, Surabaya",
            "Jl. Ahmad Yani No. 33, Malang",
            "Jl. Veteran No. 7, Yogyakarta",
            "Jl. Pahlawan No. 19, Medan",
            "Jl. Cihampelas No. 56, Bandung",
            "Jl. Kartini No. 3, Semarang",
            "Jl. Pemuda No. 27, Surabaya",
            "Jl. Raya Bogor No. 14, Depok",
            "Jl. Cendrawasih No. 9, Makassar",
            "Jl. Imam Bonjol No. 41, Medan",
            "Jl. Slamet Riyadi No. 18, Solo",
            "Jl. Ir. H. Juanda No. 25, Bandung"
        )

        val MOCK_DATA: List<WorkingPaper> = (1..30).map { i ->
            val day = 5 + (i % 20) // sebar tanggal jatuh tempo, kira-kira 2 debitur per hari
            WorkingPaper(
                taskId = "TSK-%04d".format(i),
                noContract = "CTR-2026-%05d".format(1000 + i),
                customerId = "CUST-%04d".format(4000 + i),
                debiturName = debiturNames[(i - 1) % debiturNames.size],
                alamat = addresses[(i - 1) % addresses.size],
                businessUnit = businessUnits[(i - 1) % businessUnits.size],
                dueDate = "2026-07-%02d".format(day),
                overdue = 10 + (i * 3) % 60,
                tenor = tenors[(i - 1) % tenors.size],
                unit = units[(i - 1) % units.size],
                denda = 50_000L + (i * 5_000L),
                billing = 300_000L + (i * 25_000L),
                total = 350_000L + (i * 30_000L)
            )
        }
    }
}
