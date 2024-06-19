package ingsis.group12.snippetoperations.asset.service

import ingsis.group12.snippetoperations.asset.dto.ShareDTO
import ingsis.group12.snippetoperations.asset.input.AssetInput
import ingsis.group12.snippetoperations.asset.model.Asset
import java.util.UUID

interface AssetService {
    fun createAsset(
        assetInput: AssetInput,
        userId: String,
    ): Asset

    fun getAssetById(assetId: UUID): Asset

    fun getAssets(): List<Asset>

    fun deleteAssetById(
        assetId: UUID,
        userId: String,
    ): String

    fun updateAsset(
        assetId: UUID,
        assetInput: AssetInput,
        userId: String,
    ): Asset

    fun shareAsset(
        userId: String,
        shareDTO: ShareDTO,
    )
}
