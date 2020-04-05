import { NgModule } from '@angular/core';
import { OperacionesRoutingModule } from './operaciones-routing.module';
import { OperacionesComponent } from './operaciones.component';
import { RevisionComponent } from './revision/revision.component';
import { CommonsModule } from '../commons/commons.module';
import { DevolucionesComponent } from './devoluciones/devoluciones.component';
import { ValidacionDevolucionComponent } from './devoluciones/validacion-devolucion/validacion-devolucion.component';
import { DownloadCsvService } from '../../@core/util-services/download-csv.service';
import { DonwloadFileService } from '../../@core/util-services/download-file-service';



@NgModule({
  declarations: [
    OperacionesComponent,
    RevisionComponent,
    DevolucionesComponent,
    ValidacionDevolucionComponent],
  imports: [
    OperacionesRoutingModule,
    CommonsModule,
  ],
  entryComponents: [ValidacionDevolucionComponent],
  providers: [ DownloadCsvService , DonwloadFileService ],
})
export class OperacionesModule { }
