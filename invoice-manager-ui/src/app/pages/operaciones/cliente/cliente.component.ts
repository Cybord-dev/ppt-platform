import { Component, OnInit } from '@angular/core';
import { Client } from '../../../models/client';
import { ClientsData } from '../../../@core/data/clients-data';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'ngx-cliente',
  templateUrl: './cliente.component.html',
  styleUrls: ['./cliente.component.scss']
})
export class ClienteComponent implements OnInit {

  public clientInfo : Client;
  public formInfo : any = {rfc:'',message:'',coloniaId:'*', success:''};
  public coloniaId: number=0;
  public colonias = [];
  public paises = ['México'];
  public porcentajes = {promotor:25,cliente:25,despacho:25,contacto:25};
  constructor(private clientService:ClientsData,private catalogsService:CatalogsData,private route: ActivatedRoute) { }

  ngOnInit() {
    /** recovering folio info**/
    this.route.paramMap.subscribe(route => {
      let rfc = route.get('rfc');
      this.clientService.getClientByRFC(rfc)
      .subscribe((data:Client) => {this.clientInfo = data, this.formInfo.rfc = rfc;},
      (error : HttpErrorResponse)=>{this.formInfo.message = error.error.message || `${error.statusText} : ${error.message}`; this.formInfo.status = error.status});  
      });
  }


}
