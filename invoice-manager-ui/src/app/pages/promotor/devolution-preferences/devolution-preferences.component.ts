import { Component, OnInit } from '@angular/core';
import { ClientsData } from '../../../@core/data/clients-data';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { ActivatedRoute } from '@angular/router';
import { UsersData } from '../../../@core/data/users-data';
import { Factura } from '../../../models/factura/factura';
import { PagoDevolucion } from '../../../models/pago-devolucion';
import { DevolucionValidatorService } from '../../../@core/util-services/devolucion-validator.service';
import { Client } from '../../../models/client';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { HttpErrorResponse } from '@angular/common/http';
import { DevolutionData } from '../../../@core/data/devolution-data';
import { ResourceFile } from '../../../models/resource-file';
import { FilesData } from '../../../@core/data/files-data';
import { PagoBase } from '../../../models/pago-base';
import { PaymentsData } from '../../../@core/data/payments-data';
import { User } from '../../../models/user';

@Component({
  selector: 'ngx-devolution-preferences',
  templateUrl: './devolution-preferences.component.html',
  styleUrls: ['./devolution-preferences.component.scss'],
})
export class DevolutionPreferencesComponent implements OnInit {

  public folioParam: string;
  public fileInput: any = {};
  public user: User;
  public factura: Factura= new Factura();
  public pago: PagoBase;
 
  public formParams: any = {tab: 'CLIENTE', filename: ''};
  public solicitud: PagoDevolucion = new PagoDevolucion();
  public banksCat: Catalogo[] = [];
  public refTypesCat: Catalogo[] = [];
  public messages: string[] = [];
  public clientInfo: Client = new Client();

  private referenceFile: ResourceFile;

  constructor(private clientsService: ClientsData,
    private invoiceService: InvoicesData,
    private catalogService: CatalogsData,
    private resourceService: FilesData,
    private userService: UsersData,
    private devolutionService: DevolutionData,
    private devolutionValidator: DevolucionValidatorService,
    private paymentservice: PaymentsData,
    private route: ActivatedRoute) {}


  ngOnInit() {
    this.messages = [];
    this.fileInput.value = '';
    this.userService.getUserInfo().then(user => this.user = user);
    this.catalogService.getBancos().then(banks => this.banksCat = banks);
    this.route.paramMap.subscribe(route => {
      this.folioParam = route.get('folio');
      if (this.folioParam !== '*') {
        // refactor
       /* this.invoiceService.getInvoiceByFolio(this.folioParam)
            .subscribe( invoice => {
              if ( invoice.tipoDocumento === 'Complemento') {
                this.invoiceService.getInvoiceByFolio(invoice.folioPadre).subscribe(padre => {
                  this.factura = padre;
                  this.paymentservice.getPaymentsByFolio(padre.folio).toPromise()
                  .then(pagos =>  this.pago = pagos.filter(p => p.formaPago !== 'CREDITO').find(p => p.folio === this.folioParam))
                  .then(() =>
                    this.clientsService.getClientByRFC(invoice.rfcRemitente)
                    .subscribe(client => {this.clientInfo = client; this.selectTab('CLIENTE'); }));
                });
              }else {
                this.factura = invoice;
                this.paymentservice.getPaymentsByFolio(this.folioParam).toPromise()
                  .then(pagos => this.pago = pagos.filter(p => p.formaPago !== 'CREDITO').find(p => p.folio === this.folioParam))
                  .then(() => {
                    this.clientsService.getClientByRFC(invoice.rfcRemitente)
                    .subscribe(client => {this.clientInfo = client; this.selectTab('CLIENTE'); })});
              }
            });*/
      } else {
        this.initVariables();
      }
    });
  }

  public initVariables() {
    /** INIT VARIABLES **/
    this.messages = [];
    this.fileInput.value = '';
  }

  public selectTab(tiporeceptor: string) {
    this.messages = [];
    this.formParams.tab = tiporeceptor;
   

    if(this.pago){
      this.devolutionService.findDevolutionByFolioFactAndTipoReceptor(this.folioParam, tiporeceptor)
      .subscribe(solicitud => this.solicitud = solicitud,
        (error: HttpErrorResponse) => {
          this.solicitud = new PagoDevolucion();
          this.solicitud.tipoReceptor = tiporeceptor;
          this.solicitud.monto = this.devolutionValidator
              .calculateDevolutionAmmount(this.factura, this.pago, this.clientInfo, tiporeceptor);
              if (tiporeceptor === 'CLIENTE') {
                this.solicitud.receptor = this.factura.rfcRemitente;
              }
              if (tiporeceptor === 'PROMOTOR') {
                this.solicitud.receptor = this.clientInfo.correoPromotor;
              }
              if (tiporeceptor === 'CONTACTO') {
                this.solicitud.receptor = this.clientInfo.correoContacto;
              }
          this.messages.push(error.error.message || `${error.statusText} : ${error.message}`);
        });
    } else {
      this.messages.push('No se encontraron pagos ligados a la factura, cargue primero el pago antes de solicitar la pre-devolucion.');
    }
   
  }

  public onPayFormSelected(formaPago: string) {
    if (formaPago !== '*') {
      this.catalogService.getTiposReferencia(formaPago)
        .then(types => {
          this.refTypesCat = types;
          this.solicitud.tipoReferencia = types[0].id;
        });
      if (formaPago === 'TRANSFERENCIA') {
        this.solicitud.banco = this.banksCat[0].nombre;
      } else {
        this.solicitud.banco = 'N/A';
      }
    } else {
      this.solicitud.tipoReferencia = '*';
      this.solicitud.banco = 'N/A';
    }
  }

  public fileUploadListener(event: any): void {
    this.fileInput = event.target;
    const reader = new FileReader();
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      if (file.size > 1000000) {
        alert('El archivo demasiado grande, intenta con un archivo mas pequeño.');
      } else {
        reader.readAsDataURL(file);
        reader.onload = () => {
          this.formParams.filename = file.name;
          this.referenceFile = new ResourceFile();
          this.referenceFile.tipoArchivo = 'ARCHIVO';
          this.referenceFile.tipoRecurso = 'DEVOLUCION';
          this.referenceFile.data = reader.result.toString();
        };
        reader.onerror = (error) => { this.messages.push('Error parsing image file'); };
      }
    }
  }

  public devolutionRequest(solicitud: PagoDevolucion) {
    this.messages = [];
    this.fileInput.value = '';
    solicitud.solicitante = this.user.email;
    solicitud.status = 'SOLICITUD';
    solicitud.folioFactura = this.folioParam;
    if (solicitud.formaPago === 'PAGO_MULTIPLE') {
      solicitud.referencia = this.formParams.filename;
    }
    this.messages = this.devolutionValidator.validateDevolution(this.solicitud.monto, solicitud);
    if (this.messages.length === 0) {
      this.devolutionService.requestDevolution(solicitud)
        .subscribe( request => {this.solicitud = request;
          if (solicitud.formaPago === 'PAGO_MULTIPLE') {
            this.referenceFile.referencia = request.id.toString();
          this.resourceService.insertResourceFile(this.referenceFile)
          .subscribe((resource: ResourceFile) => console.log(resource),
          (error: HttpErrorResponse) => this.messages.push(error.error.message
            || `${error.statusText} : ${error.message}`));
          }
        }, (error: HttpErrorResponse) => this.messages.push(error.error.message
            || `${error.statusText} : ${error.message}`));
    } 
  }


}
