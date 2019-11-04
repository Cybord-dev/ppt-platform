import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  NbActionsModule,
  NbButtonModule,
  NbCardModule,
  NbCheckboxModule,
  NbDatepickerModule,
  NbInputModule,
  NbRadioModule,
  NbSelectModule,
  NbUserModule,
  NbStepperModule,
  NbDialogModule,
  NbIconModule
} from '@nebular/theme';

import { TesoreriaRoutingModule } from './tesoreria-routing.module';
import { TesoreriaComponent } from './tesoreria.component';
import { PagosComponent } from './pagos/pagos.component';
import { DevolucionesComponent } from './devoluciones/devoluciones.component';

import {DownloadCsvService } from '../../@core/back-services/download-csv.service';

@NgModule({
  declarations: [TesoreriaComponent,PagosComponent,DevolucionesComponent],
  imports: [
    TesoreriaRoutingModule,

    CommonModule,
    FormsModule,
    NbActionsModule,
    NbButtonModule,
    NbCardModule,
    NbCheckboxModule,
    NbDatepickerModule, 
    NbIconModule,
    NbInputModule,
    NbRadioModule,
    NbSelectModule,
    NbUserModule,
    NbStepperModule,
    NbDialogModule,
  ],
  providers:[DownloadCsvService]
})
export class TesoreriaModule { }
