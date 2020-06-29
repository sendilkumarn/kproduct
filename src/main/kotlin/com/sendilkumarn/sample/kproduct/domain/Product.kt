package com.sendilkumarn.sample.kproduct.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.sendilkumarn.sample.kproduct.domain.enumeration.Size
import io.swagger.annotations.ApiModel
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * Entities for kproduct microservice
 */
@ApiModel(description = "Entities for kproduct microservice")
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @get: NotNull
    @get: DecimalMin(value = "0")
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    var price: BigDecimal? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    var size: Size? = null,

    @Lob
    @Column(name = "image")
    var image: ByteArray? = null,

    @Column(name = "image_content_type")
    var imageContentType: String? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["products"], allowSetters = true)
    var productCategory: ProductCategory? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Product) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Product{" +
        "id=$id" +
        ", name='$name'" +
        ", description='$description'" +
        ", price=$price" +
        ", size='$size'" +
        ", image='$image'" +
        ", imageContentType='$imageContentType'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
