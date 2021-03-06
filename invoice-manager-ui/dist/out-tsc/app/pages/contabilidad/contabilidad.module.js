import * as tslib_1 from "tslib";
import { NgModule } from '@angular/core';
import { NbDialogModule } from '@nebular/theme';
import { ContabilidadRoutingModule } from './contabilidad-routing.module';
import { ContabilidadComponent } from './contabilidad.component';
import { PreCfdiComponent } from './pre-cfdi/pre-cfdi.component';
import { ReportesComponent } from './reportes/reportes.component';
import { InvoiceGeneratorComponent } from './invoice-generator/invoice-generator.component';
import { InvoiceRequestComponent } from './invoice-generator/invoice-request/invoice-request.component';
import { TransferenciasComponent } from './transferencias/transferencias.component';
import { CommonsModule } from '../commons/commons.module';
let ContabilidadModule = class ContabilidadModule {
};
ContabilidadModule = tslib_1.__decorate([
    NgModule({
        declarations: [ContabilidadComponent,
            PreCfdiComponent,
            ReportesComponent,
            InvoiceGeneratorComponent,
            InvoiceRequestComponent,
            TransferenciasComponent],
        imports: [
            ContabilidadRoutingModule,
            CommonsModule,
            NbDialogModule.forChild(),
        ],
        entryComponents: [InvoiceRequestComponent]
    })
], ContabilidadModule);
export { ContabilidadModule };
//# sourceMappingURL=contabilidad.module.js.map