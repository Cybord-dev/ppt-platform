import { Component, OnInit, Input } from '@angular/core';
import { Factura } from '../../../models/factura/factura';
import { User } from '../../../models/user';
import { PagoBase } from '../../../models/pago-base';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { Cuenta } from '../../../models/cuenta';
import { PaymentsData } from '../../../@core/data/payments-data';
import { CuentasData } from '../../../@core/data/cuentas-data';
import { FilesData } from '../../../@core/data/files-data';
import { PagosValidatorService } from '../../../@core/util-services/pagos-validator.service';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { HttpErrorResponse } from '@angular/common/http';
import { ResourceFile } from '../../../models/resource-file';

@Component({
  selector: 'ngx-pago-factura',
  templateUrl: './pago-factura.component.html',
  styleUrls: ['./pago-factura.component.scss'],
})
export class PagoFacturaComponent implements OnInit {

  @Input() factura: Factura;
  @Input() user: User;

  public fileInput: any;

  public paymentForm = { payType: '*', bankAccount: '*', filename: ''};
  public newPayment: PagoBase = new PagoBase();
  public invoicePayments: PagoBase[] = [];
  public paymentSum: number = 0;
  public payErrorMessages: string[] = [];
  public payTypeCat: Catalogo[] = [];
  public cuentas: Cuenta[];
  public loading: boolean = false;

  constructor(private paymentsService: PaymentsData,
    private accountsService: CuentasData,
    private fileService: FilesData,
    private paymentValidator: PagosValidatorService,
    private invoiceService: InvoicesData) { }

  ngOnInit() {
    this.newPayment.moneda = 'MXN';
    if (this.factura !== undefined && this.factura.folio !== undefined) {
      this.paymentsService.getFormasPago(this.user.roles.map(r => r.role))
        .subscribe(payTypes => this.payTypeCat = payTypes);
      this.paymentsService.getPaymentsByFolio(this.factura.folio)
              .subscribe((payments: PagoBase[]) => {
                this.invoicePayments = payments;
                this.paymentSum = this.paymentValidator.getPaymentAmmount(payments);
                if (payments.find(p => p.formaPago === 'CREDITO') !== undefined
                      && this.payTypeCat.find(c => c.id === 'CREDITO') !== undefined) {
                    this.payTypeCat.pop();//removes credit as option, there is no posible require a second credit
                }
                });
    }
  }

  /******* PAGOS ********/

  onPaymentCoinSelected(clave: string) {
    this.newPayment.moneda = clave;
  }

  onPaymentTypeSelected(clave: string) {
    this.newPayment.formaPago = clave;
    if (clave === 'EFECTIVO' || clave === 'CHEQUE' || clave === '*') {
      this.cuentas = [ new Cuenta('N/A', 'No aplica', 'Sin especificar')];
      this.paymentForm.bankAccount = 'N/A';
      this.newPayment.banco = 'No aplica';
            this.newPayment.cuenta = 'Sin especificar';
    }else {
      this.accountsService.getCuentasByCompany(this.factura.rfcEmisor)
          .subscribe(cuentas => {
            this.cuentas = cuentas;
            this.paymentForm.bankAccount = cuentas[0].id;
            this.newPayment.banco = cuentas[0].banco;
            this.newPayment.cuenta = cuentas[0].cuenta;
          });
    }
  }

  onPaymentBankSelected(clave: string) {
    this.newPayment.banco = clave;
  }

  fileUploadListener(event: any): void {
    this.fileInput = event.target;
    const reader = new FileReader();
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      if (file.size > 1000000) {
        alert('El archivo demasiado grande, intenta con un archivo mas pequeño.');
      } else {
        reader.readAsDataURL(file);
        reader.onload = () => {
          this.paymentForm.filename = file.name;
          this.newPayment.documento = reader.result.toString();
        };
        reader.onerror = (error) => { this.payErrorMessages.push('Error parsing image file'); };
      }
    }
  }

  deletePayment(paymentId) {
    this.paymentsService.deletePayment(this.factura.folio, paymentId).subscribe(
      result => {
        this.paymentsService.getPaymentsByFolio(this.factura.folio)
          .subscribe(payments => {
            this.invoicePayments = payments;
            this.paymentSum = this.paymentValidator.getPaymentAmmount(payments);
          });
        this.invoiceService.getComplementosInvoice(this.factura.folio)
          .subscribe(complementos => this.factura.complementos = complementos);
      }, (error: HttpErrorResponse) =>
      this.payErrorMessages.push(error.error.message || `${error.statusText} : ${error.message}`));
  }

  sendPayment() {
    this.newPayment.folioPadre = this.factura.folio;
    this.newPayment.folio = this.factura.folio;
    this.newPayment.tipoPago = 'INGRESO';
    this.newPayment.acredor = this.factura.razonSocialEmisor;
    this.newPayment.deudor = this.factura.razonSocialRemitente;
    this.newPayment.solicitante = this.user.email;
    const payment  = {... this.newPayment};
    this.payErrorMessages = this.paymentValidator.validatePago(payment, this.invoicePayments, this.factura.cfdi);
    if (this.payErrorMessages.length === 0) {
      this.loading = true;
      this.paymentsService.insertNewPayment(this.factura.folio, payment).subscribe(
        result => {
          this.newPayment = new PagoBase();
          const resourceFile = new ResourceFile();
          resourceFile.tipoArchivo = 'IMAGEN';
          resourceFile.tipoRecurso = 'PAGO';
          resourceFile.referencia  = `${result.id}_${result.folio}`;
          resourceFile.data = payment.documento;
          this.fileService.insertResourceFile(resourceFile).subscribe(response => console.log(response));
          this.paymentsService.getPaymentsByFolio(this.factura.folio)
          .subscribe((payments: PagoBase[]) => { this.invoicePayments = payments;
            this.paymentSum = this.paymentValidator.getPaymentAmmount(payments);
            this.loading = false;
             if (payments.find(p => p.formaPago === 'CREDITO') !== undefined
                      && this.payTypeCat.find(c => c.id === 'CREDITO') !== undefined) {
                    this.payTypeCat.pop();//removes credit as option, there is no posible require a second credit
              }
          });
          this.invoiceService.getComplementosInvoice(this.factura.folio)
          .subscribe(complementos => this.factura.complementos = complementos);
        }, (error: HttpErrorResponse) => {
          this.loading = false;
          this.payErrorMessages.push(error.error.message || `${error.statusText} : ${error.message}`);
        });
    }
    this.newPayment = new PagoBase();
    this.paymentForm = { payType: '*', bankAccount: '*', filename: ''};
    if (this.fileInput !== undefined) {
      this.fileInput.value = '';
    }
  }
}
