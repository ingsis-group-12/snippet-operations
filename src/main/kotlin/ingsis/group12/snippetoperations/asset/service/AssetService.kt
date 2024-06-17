package ingsis.group12.snippetoperations.asset.service

import ingsis.group12.snippetoperations.asset.dto.ShareDTO
import ingsis.group12.snippetoperations.asset.input.SnippetInput
import ingsis.group12.snippetoperations.asset.model.Asset
import java.util.UUID

interface AssetService {
    fun createAsset(
        assetInput: SnippetInput,
        userId: String,
    ): Asset

    fun getAssetById(assetId: UUID): Asset

    fun getAssets(): List<Asset>

    fun deleteAssetById(assetId: UUID): String

    fun shareAsset(
        userId: String,
        shareDTO: ShareDTO,
    )
}
