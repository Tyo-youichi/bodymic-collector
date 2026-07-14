package org.example.collrecord.data

import kotlinx.serialization.json.Json
import org.example.collrecord.model.WorkingPaper

interface WorkingPaperRepository {
    suspend fun getTaskList(): List<WorkingPaper>
}

/**
 * Mock implementation — data working paper di-embed sebagai JSON string,
 * supaya tetap murni commonMain (nggak butuh akses file/asset platform-specific).
 *
 * Nanti kalau backend beneran siap, tinggal buat RemoteWorkingPaperRepository
 * yang GET ke ApiConfig.BASE_URL (pakai HttpClient dari createHttpClient()),
 * tanpa perlu ubah ViewModel/UI yang consume interface ini.
 */
class MockWorkingPaperRepository : WorkingPaperRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getTaskList(): List<WorkingPaper> {
        return json.decodeFromString(MOCK_JSON)
    }

    companion object {
        private const val MOCK_JSON = """
        [
          {
            "task_id": "TSK-0001",
            "no_contract": "CTR-2026-00123",
            "customer_id": "CUST-4021",
            "debitur_name": "Budi Santoso",
            "business_unit": "Motor",
            "due_date": "2026-06-10",
            "overdue": 34,
            "tenor": 24,
            "unit": "Honda Beat 2022",
            "denda": 150000,
            "billing": 850000,
            "total": 1000000
          },
          {
            "task_id": "TSK-0002",
            "no_contract": "CTR-2026-00456",
            "customer_id": "CUST-4088",
            "debitur_name": "Siti Aminah",
            "business_unit": "Elektronik",
            "due_date": "2026-06-15",
            "overdue": 29,
            "tenor": 12,
            "unit": "Kulkas 2 Pintu",
            "denda": 75000,
            "billing": 425000,
            "total": 500000
          }
        ]
        """
    }
}
