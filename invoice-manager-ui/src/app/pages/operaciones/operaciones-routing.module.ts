import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { OperacionesComponent } from './operaciones.component';
import { RevisionComponent } from './revision/revision.component';
import { ClientesComponent } from '../commons/clientes/clientes.component';
import { ClienteComponent } from '../commons/cliente/cliente.component';
import { EmpresasComponent } from '../commons/empresas/empresas.component';
import { EmpresaComponent } from '../commons/empresa/empresa.component';
import { DevolucionesComponent } from './devoluciones/devoluciones.component';
import { InvoiceReportsComponent } from '../commons/invoice-reports/invoice-reports.component';
import { DevolutionsDetailsComponent } from '../commons/devolutions-details/devolutions-details.component';
import { LineaXComponent } from './linea-x/linea-x.component';
import { MulticomplementosComponent } from '../commons/multicomplementos/multicomplementos.component';
import { PagosFacturaComponent } from '../commons/pagos-factura/pagos-factura.component';
import { PagosComponent } from '../commons/pagos/pagos.component';
import { CuentasBancariasComponent } from '../commons/cuentas-bancarias/cuentas-bancarias.component';
import { CuentaBancariaComponent } from '../commons/cuenta-bancaria/cuenta-bancaria.component';

const routes: Routes = [{
  path: '',
  component: OperacionesComponent,
  children: [{
      path: 'devoluciones',
      component : DevolucionesComponent,
    },
    {
      path: 'clientes',
      component: ClientesComponent,
    }, {
      path: 'cliente/:rfc',
      component: ClienteComponent,
    }, {
      path: 'cliente/:rfc/:promotor',
      component: ClienteComponent,
    }, {
      path: 'empresas',
      component: EmpresasComponent,
    }, {
      path: 'empresa/:rfc',
      component: EmpresaComponent,
    }, {
      path: 'reportes',
      component : InvoiceReportsComponent,
    }, {
      path: 'revision/:folio',
      component : RevisionComponent,
    }, {
      path: 'cfdi/:linea/:folio',
      component : LineaXComponent,
    }, {
      path: 'multicomplementos',
      component : MulticomplementosComponent,
    },
    {
      path: 'facturas/:folio/devoluciones',
      component: DevolutionsDetailsComponent,
    }, {
      path: 'pago-facturas',
      component: PagosFacturaComponent,
    },
    {
      path: 'pagos',
      component: PagosComponent,
    },
    {
      path: 'cuentas-bancarias',
      component: CuentasBancariasComponent,
    },
    {
      path: 'cuenta-bancaria/:empresa/:cuenta',
      component: CuentaBancariaComponent,
    }
  ]}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class OperacionesRoutingModule { }
