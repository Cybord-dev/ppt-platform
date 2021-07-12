import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  NbActionsModule,
  NbButtonModule,
  NbCardModule,
  NbTabsetModule,
  NbCheckboxModule,
  NbDatepickerModule,
  NbInputModule,
  NbRadioModule,
  NbSelectModule,
  NbUserModule,
  NbStepperModule,
  NbDialogModule,
  NbIconModule,
  NbTreeGridModule,
  NbSpinnerModule,
} from '@nebular/theme';

import { ClientesComponent } from './clientes/clientes.component';
import { ClienteComponent } from './cliente/cliente.component';
import { EmpresaComponent } from './empresa/empresa.component';
import { EmpresasComponent } from './empresas/empresas.component';
import { PagosFacturaComponent } from './pagos-factura/pagos-factura.component';
import { PagosComponent } from './pagos/pagos.component';
import { ValidacionPagoComponent } from './pagos/validacion-pago/validacion-pago.component';
import { ConceptosComponent } from './conceptos/conceptos.component';
import { InvoiceReportsComponent } from './invoice-reports/invoice-reports.component';
import { DevolutionsDetailsComponent } from './devolutions-details/devolutions-details.component';
import { CfdiComponent } from './cfdi/cfdi.component';
import { AsignacionPagosComponent } from './asignacion-pagos/asignacion-pagos.component';
import {AutocompleteLibModule} from 'angular-ng-autocomplete';
import { PagoFacturaComponent } from './pago-factura/pago-factura.component';
import { GenerarComplementoComponent } from './generar-complemento/generar-complemento.component';
import { MulticomplementosComponent } from './multicomplementos/multicomplementos.component';
import { CuentasBancariasComponent } from './cuentas-bancarias/cuentas-bancarias.component';
import { CuentaBancariaComponent } from './cuenta-bancaria/cuenta-bancaria.component';

@NgModule({
  declarations: [
    ClientesComponent,
    ClienteComponent,
    EmpresaComponent,
    EmpresasComponent,
    PagosFacturaComponent,
    PagosComponent,
    ValidacionPagoComponent,
    ConceptosComponent,
    InvoiceReportsComponent,
    DevolutionsDetailsComponent,
    CfdiComponent,
    AsignacionPagosComponent,
    PagoFacturaComponent,
    GenerarComplementoComponent,
    MulticomplementosComponent,
    CuentasBancariasComponent,
    CuentasBancariasComponent,
    CuentaBancariaComponent
  ],
  imports: [
    CommonModule,
    AutocompleteLibModule,
    FormsModule,
    NbTabsetModule,
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
    NbDialogModule.forChild(),
    NbIconModule,
    NbSpinnerModule,
    NbTreeGridModule,
  ],
  exports: [
    ClientesComponent,
    ClienteComponent,
    EmpresaComponent,
    EmpresasComponent,
    InvoiceReportsComponent,
    DevolutionsDetailsComponent,
    PagosFacturaComponent,
    PagosComponent,
    ValidacionPagoComponent,
    PagoFacturaComponent,
    GenerarComplementoComponent,
    MulticomplementosComponent,
    ConceptosComponent,
    CfdiComponent,
    AsignacionPagosComponent,
    CuentasBancariasComponent,
    FormsModule,
    CommonModule,
    NbTabsetModule,
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
    NbIconModule,
    NbSpinnerModule,
    NbTreeGridModule,
  ]})
export class CommonsModule { }
