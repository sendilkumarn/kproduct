package com.sendilkumarn.sample.kproduct.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.sendilkumarn.sample.kproduct.domain.enumeration.OrderItemStatus
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A OrderItem.
 */
@Entity
@Table(name = "order_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @get: Min(value = 0)
    @Column(name = "quantity", nullable = false)
    var quantity: Int? = null,

    @get: NotNull
    @get: DecimalMin(value = "0")
    @Column(name = "total_price", precision = 21, scale = 2, nullable = false)
    var totalPrice: BigDecimal? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OrderItemStatus? = null,

    @ManyToOne(optional = false) @NotNull
    @JsonIgnoreProperties(value = ["orderItems"], allowSetters = true)
    var product: Product? = null,

    @ManyToOne(optional = false) @NotNull
    @JsonIgnoreProperties(value = ["orderItems"], allowSetters = true)
    var order: ProductOrder? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderItem) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "OrderItem{" +
        "id=$id" +
        ", quantity=$quantity" +
        ", totalPrice=$totalPrice" +
        ", status='$status'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
