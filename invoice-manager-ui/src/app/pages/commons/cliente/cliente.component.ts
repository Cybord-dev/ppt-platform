import { Component, OnInit } from '@angular/core';
import { Client } from '../../../models/client';
import { ClientsData } from '../../../@core/data/clients-data';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { ZipCodeInfo } from '../../../models/zip-code-info';
import { UsersData } from '../../../@core/data/users-data';

@Component({
  selector: 'ngx-cliente',
  templateUrl: './cliente.component.html',
  styleUrls: ['./cliente.component.scss']
})
export class ClienteComponent implements OnInit {

  public isPromotor: boolean = false;
  public clientInfo: Client;
  public formInfo: any = {rfc:'',message:'',coloniaId:'*', success:''};
  public coloniaId: number= 0;
  public colonias = [];
  public paises = ['México'];
  constructor(private clientService: ClientsData,
              private userService: UsersData,
              private catalogsService: CatalogsData,
              private route: ActivatedRoute,
              private router: Router) { }

  ngOnInit() {
    this.isPromotor = (this.router.url.indexOf('/promotor') > 0) ? true : false;
    this.clientInfo = new Client();
    /** recovering folio info**/
    this.route.paramMap.subscribe(route => {
      const rfc = route.get('rfc');
      if (rfc !== '*') {
        this.clientService.getClientByRFC(rfc)
        .subscribe((client: Client) => {this.clientInfo = client, this.formInfo.rfc = rfc; },
        (error: HttpErrorResponse) => {this.formInfo.message = error.error.message ||
                  `${error.statusText} : ${error.message}`; this.formInfo.status = error.status;});
        }});
  }

  public updateClient() {
    this.formInfo.success = '';
    this.formInfo.message = '';
    this. validatePercentages();
    this.clientService.updateClient(this.clientInfo).subscribe(client => { this.formInfo.success = 'Cliente actualizado exitosamente'; this.clientInfo = client; },
      (error: HttpErrorResponse) => { this.formInfo.message = error.error.message || `${error.statusText} : ${error.message}`; this.formInfo.status = error.status });
  }

  public insertClient() {
    this.formInfo.success = '';
    this.formInfo.message = '';
    this. validatePercentages();
    this.userService.getUserInfo().toPromise().then(user => this.clientInfo.correoPromotor = user.email)
      .then(() => {
        this.clientService.insertNewClient(this.clientInfo).subscribe(client => { this.formInfo.success = 'Cliente guardado exitosamente'; this.clientInfo = client; },
          (error: HttpErrorResponse) => { this.formInfo.message = error.error.message || `${error.statusText} : ${error.message}`; this.formInfo.status = error.status });
      });
  }

  public zipCodeInfo(zc: string){
    if(zc.length > 4 && zc.length < 6) {
      this.colonias = [];
      this.catalogsService.getZipCodeInfo(zc).subscribe(
          (data:ZipCodeInfo) => {
          this.clientInfo.informacionFiscal.estado = data.estado;
          this.clientInfo.informacionFiscal.municipio = data.municipio;
          this.colonias = data.colonias; 
          this.clientInfo.informacionFiscal.localidad = data.colonias[0];
          if (data.colonias.length < 1 ) {
            alert(`No se ha encontrado información pata el codigo postal ${zc}`);
          }
          }, (error: HttpErrorResponse) => alert(error.error.message || error.statusText));
    }
  }

  public onLocation(index:string){
    this.clientInfo.informacionFiscal.localidad = this.colonias[index];
  }

  public validatePercentages() {
    if ( this.clientInfo.correoContacto === undefined || this.clientInfo.correoContacto.length < 1) {
      this.clientInfo.correoContacto = 'Sin asignar';
      this.clientInfo.porcentajeContacto = 0;
      this.clientInfo.porcentajeDespacho = 16 - this.clientInfo.porcentajeCliente - this.clientInfo.porcentajePromotor;
    }
  }

  public toggleOn() {
    this.clientInfo.activo = true;
    this.updateClient();
  }

  public toggleOff() {
    this.clientInfo.activo = false;
    this.updateClient();
  }

}