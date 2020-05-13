import { NgModule } from '@angular/core';


import { PromotorRoutingModule } from './promotor-routing.module';
import { PromotorComponent } from './promotor.component';
import { PreCfdiComponent } from './pre-cfdi/pre-cfdi.component';
import { DevolucionesComponent } from '../commons/devoluciones/devoluciones.component';

import {DownloadCsvService } from '../../@core/util-services/download-csv.service';
import { DonwloadFileService } from '../../@core/util-services/download-file-service';
import { CommonsModule } from '../commons/commons.module';
import { DevolutionPreferencesComponent } from './devolution-preferences/devolution-preferences.component';


@NgModule({
  declarations: [PromotorComponent,
    PreCfdiComponent,
    DevolutionPreferencesComponent],
  imports: [
    PromotorRoutingModule,
    CommonsModule,
  ],
  providers: [ DownloadCsvService , DonwloadFileService ],
})
export class PromotorModule { }
