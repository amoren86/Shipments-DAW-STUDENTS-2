package cat.institutmarianao.shipments.model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/* Lombok */
@Data
@EqualsAndHashCode(callSuper = true)
public class Delivery extends Action implements Serializable {
	private static final long serialVersionUID = 1L;

	public Delivery() {
		type = Type.DELIVERY;
	}
}
