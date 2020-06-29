package com.sendilkumarn.sample.kproduct.domain

import com.sendilkumarn.sample.kproduct.domain.enumeration.OrderStatus
import java.io.Serializable
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A ProductOrder.
 */
@Entity
@Table(name = "product_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ProductOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "placed_date", nullable = false)
    var placedDate: Instant? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OrderStatus? = null,

    @get: NotNull
    @Column(name = "code", nullable = false)
    var code: String? = null,

    @Column(name = "invoice_id")
    var invoiceId: Long? = null,

    @get: NotNull
    @Column(name = "customer", nullable = false)
    var customer: String? = null,

    @OneToMany(mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    var orderItems: MutableSet<OrderItem> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addOrderItem(orderItem: OrderItem): ProductOrder {
        this.orderItems.add(orderItem)
        orderItem.order = this
        return this
    }

    fun removeOrderItem(orderItem: OrderItem): ProductOrder {
        this.orderItems.remove(orderItem)
        orderItem.order = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductOrder) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ProductOrder{" +
        "id=$id" +
        ", placedDate='$placedDate'" +
        ", status='$status'" +
        ", code='$code'" +
        ", invoiceId=$invoiceId" +
        ", customer='$customer'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
