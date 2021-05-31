import * as tslib_1 from "tslib";
import { Injectable } from '@angular/core';
import { Impuesto } from '../../models/factura/impuesto';
let CfdiValidatorService = class CfdiValidatorService {
    constructor() { }
    buildConcepto(concepto) {
        concepto.importe = concepto.cantidad * concepto.valorUnitario;
        const base = concepto.importe - concepto.descuento;
        if (concepto.iva) {
            const impuesto = base * 0.16;
            concepto.impuestos = [new Impuesto('002', '0.160000', base, impuesto)];
        }
        return concepto;
    }
    validarConcepto(concepto) {
        const messages = [];
        if (concepto.cantidad <= 0) {
            messages.push('La cantidad requerida debe ser mayor a 0');
        }
        if (concepto.claveProdServ === undefined || concepto.claveProdServ === '*') {
            messages.push('La clave producto servicio del concepto es un valor requerido.');
        }
        if (concepto.claveUnidad === undefined || concepto.claveUnidad === '*') {
            messages.push('La clave de unidad y la unidad son campos requeridos.');
        }
        if (concepto.descripcion === undefined || concepto.descripcion.length < 1) {
            messages.push('La descripción del concepto es un valor requerido.');
        }
        if (concepto.valorUnitario <= 0) {
            messages.push('El valor unitario del  concepto no puede ser menor igual a 0 pesos.');
        }
        return messages;
    }
    calcularImportes(cfdi) {
        let total = 0;
        let subtotal = 0;
        for (const concepto of cfdi.conceptos) {
            const base = concepto.importe - concepto.descuento;
            subtotal += base;
            let impuesto = 0;
            for (const imp of concepto.impuestos) {
                impuesto = (imp.importe * 3 + impuesto * 3) / 3;
            }
            total += Math.round(100 * (base * 3 + impuesto * 3) / 3) / 100;
        }
        cfdi.total = total;
        cfdi.subtotal = subtotal;
        return cfdi;
    }
    validarCfdi(cfdi) {
        const messages = [];
        if (cfdi.receptor === undefined || cfdi.receptor.rfc === undefined || cfdi.receptor.rfc.length < 8) {
            messages.push('La información del receptor es un valor solicitado');
        }
        if (cfdi.emisor === undefined || cfdi.emisor.rfc === undefined || cfdi.emisor.rfc.length < 8) {
            messages.push('La información del emisor es un valor solicitado');
        }
        if (cfdi.receptor.usoCfdi === undefined || cfdi.receptor.usoCfdi === '*') {
            messages.push('El uso del CFDI es un campo requerido.');
        }
        if (cfdi.moneda === undefined) {
            messages.push('La moneda es un campo requerido.');
        }
        if (cfdi.formaPago === undefined || cfdi.formaPago === '*') {
            messages.push('La forma de pago es un campo requerido.');
        }
        if (cfdi.metodoPago === undefined || cfdi.metodoPago === '*') {
            messages.push('El metodo de pago es un campo requerido.');
        }
        if (cfdi.conceptos.length < 1) {
            messages.push('La factura debe contener a menos 1 concepto a declarar.');
        }
        if (cfdi.total > 2000 && cfdi.formaPago === '01') {
            messages.push('En pagos en efectivo el monto a facturar no debe de ser superior a 2000 pesos');
        }
        return messages;
    }
};
CfdiValidatorService = tslib_1.__decorate([
    Injectable({
        providedIn: 'root',
    }),
    tslib_1.__metadata("design:paramtypes", [])
], CfdiValidatorService);
export { CfdiValidatorService };
//# sourceMappingURL=cfdi-validator.service.js.map