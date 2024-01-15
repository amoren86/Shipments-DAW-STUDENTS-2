package cat.institutmarianao.shipmentsws.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.institutmarianao.shipmentsws.ShipmentswsApplication;
import cat.institutmarianao.shipmentsws.model.Action;
import cat.institutmarianao.shipmentsws.model.Shipment;
import cat.institutmarianao.shipmentsws.model.Shipment.Category;
import cat.institutmarianao.shipmentsws.model.Shipment.Status;
import cat.institutmarianao.shipmentsws.model.User;
import cat.institutmarianao.shipmentsws.model.dto.ActionDto;
import cat.institutmarianao.shipmentsws.model.dto.ShipmentDto;
import cat.institutmarianao.shipmentsws.services.ActionService;
import cat.institutmarianao.shipmentsws.services.ShipmentService;
import cat.institutmarianao.shipmentsws.services.UserService;
import cat.institutmarianao.shipmentsws.validation.groups.OnActionCreate;
import cat.institutmarianao.shipmentsws.validation.groups.OnShipmentCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/* Swagger */
@Tag(name = "Shipment", description = "ShipmentController API")
/**/
@RestController
@RequestMapping("/shipments")
@Validated
public class ShipmentController {

	@Autowired
	private UserService userService;

	@Autowired
	private ShipmentService shipmentService;

	@Autowired
	private ActionService actionService;

	@Autowired
	private ConversionService conversionService;

	/* Swagger */
	@Operation(summary = "Find all shipments")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ShipmentDto.class))) }, description = "Shipments retrieved ok")
	/**/
	@GetMapping("/find/all")
	public List<ShipmentDto> findAll(@RequestParam(value = "status", required = false) Status status,
			@RequestParam(value = "receivedBy", required = false) String receivedBy,
			@RequestParam(value = "courierAssigned", required = false) String courierAssigned,
			@RequestParam(value = "category", required = false) Category category,
			@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = ShipmentswsApplication.DATE_PATTERN) @Parameter(description = ShipmentswsApplication.DATE_PATTERN) Date from,
			@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = ShipmentswsApplication.DATE_PATTERN) @Parameter(description = ShipmentswsApplication.DATE_PATTERN) Date to) {

		return findAllReceptions(status, receivedBy, courierAssigned, category, from, to);
	}

	/* Swagger */
	@Operation(summary = "Find all PENDING shipments")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ShipmentDto.class))) }, description = "Shipments retrieved ok")
	/**/
	@GetMapping("/find/all/PENDING")
	public List<ShipmentDto> findAllPending(@RequestParam(value = "receivedBy", required = false) String receivedBy,
			@RequestParam(value = "courierAssigned", required = false) String courierAssigned,
			@RequestParam(value = "category", required = false) Category category,
			@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = ShipmentswsApplication.DATE_PATTERN) @Parameter(description = ShipmentswsApplication.DATE_PATTERN) Date from,
			@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = ShipmentswsApplication.DATE_PATTERN) @Parameter(description = ShipmentswsApplication.DATE_PATTERN) Date to) {

		return findAllReceptions(Status.PENDING, receivedBy, courierAssigned, category, from, to);
	}

	/* Swagger */
	@Operation(summary = "Find all IN_PROCESS shipments")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ShipmentDto.class))) }, description = "Shipments retrieved ok")
	/**/
	@GetMapping("/find/all/IN_PROCESS")
	public List<ShipmentDto> findAllInProcess(@RequestParam(value = "receivedBy", required = false) String receivedBy,
			@RequestParam(value = "courierAssigned", required = false) String courierAssigned,
			@RequestParam(value = "category", required = false) Category category,
			@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = ShipmentswsApplication.DATE_PATTERN) @Parameter(description = ShipmentswsApplication.DATE_PATTERN) Date from,
			@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = ShipmentswsApplication.DATE_PATTERN) @Parameter(description = ShipmentswsApplication.DATE_PATTERN) Date to) {

		return findAllReceptions(Status.IN_PROCESS, receivedBy, courierAssigned, category, from, to);
	}

	/* Swagger */
	@Operation(summary = "Get shipment by id")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ShipmentDto.class)) }, description = "Shipment retrieved ok")
	@ApiResponse(responseCode = "404", content = {
			@Content(mediaType = "application/json") }, description = "Shipment not found")
	/**/
	@GetMapping("/get/by/id/{shipmentId}")
	public ShipmentDto findById(@PathVariable("shipmentId") @Positive Long shipmentId) {
		Shipment shipment = shipmentService.getById(shipmentId);
		return conversionService.convert(shipment, ShipmentDto.class);
	}

	/* Swagger */
	@Operation(summary = "Get shipment tracking by shipment id")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ActionDto.class))) }, description = "Tracking retrieved ok")
	/**/
	@GetMapping("/find/tracking/by/id/{shipmentId}")
	public List<ActionDto> findTrackingByTicketId(@PathVariable("shipmentId") @Positive Long shipmentId) {

		List<Action> actions = actionService.findByShipmentId(shipmentId);

		List<ActionDto> actionsDto = new ArrayList<>(actions.size());
		for (Action action : actions) {
			ActionDto actionDto = conversionService.convert(action, ActionDto.class);
			actionsDto.add(actionDto);
		}
		return actionsDto;
	}

	/* Swagger */
	@Operation(summary = "Save a shipment")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ShipmentDto.class)) }, description = "Shipment saved ok")
	/**/
	@PostMapping("/save")
	@Validated(OnShipmentCreate.class)
	public ShipmentDto save(
			@Parameter(schema = @Schema(implementation = ShipmentDto.class)) @RequestBody @Valid ShipmentDto shipmentDto) {

		return conversionService.convert(shipmentService.save(conversionService.convert(shipmentDto, Shipment.class)),
				ShipmentDto.class);
	}

	/* Swagger */
	@Operation(summary = "Save an action of a shipment (in its tracking)")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ActionDto.class)) }, description = "Action saved ok")
	/**/
	@PostMapping("/save/action")
	@Validated(OnActionCreate.class)
	public ActionDto saveAction(
			@Parameter(schema = @Schema(implementation = ActionDto.class)) @RequestBody @Valid ActionDto actionDto) {

		Action action = conversionService.convert(actionDto, Action.class);
		Action savedAction = actionService.save(action);
		return conversionService.convert(savedAction, ActionDto.class);
	}

	/* Swagger */
	@Operation(summary = "Delete a shipment")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json") }, description = "Shipment deleted ok")
	/**/
	@DeleteMapping("/delete/by/id/{shipment_id}")
	public void deleteById(@PathVariable("shipment_id") @Positive Long shipmentId) {

		shipmentService.deleteById(shipmentId);
	}

	private List<ShipmentDto> findAllReceptions(Status status, String receivedBy, String courierAssigned,
			Category category, Date from, Date to) {

		User receptionist = null;
		if (receivedBy != null) {
			receptionist = userService.getByUsername(receivedBy);
		}

		User courier = null;
		if (courierAssigned != null) {
			courier = userService.getByUsername(courierAssigned);
		}

		List<Shipment> actions = shipmentService.findAll(status, receptionist, courier, category, from, to);

		List<ShipmentDto> shipmentsDto = new ArrayList<>(actions.size());
		for (Shipment action : actions) {
			ShipmentDto shipmentDto = conversionService.convert(action, ShipmentDto.class);
			shipmentsDto.add(shipmentDto);
		}
		return shipmentsDto;
	}
}
