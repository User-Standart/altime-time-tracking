package br.com.altave.api.controller;

import br.com.altave.api.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Endpoint para gráfico de pizza: funcionários por empresa
     */
    @GetMapping("/funcionarios-por-empresa")
    public Map<String, Integer> getFuncionariosPorEmpresa() {
        return dashboardService.getFuncionariosPorEmpresa();
    }

    /**
     * Lista de empresas (usado para preencher select no frontend)
     */
    @GetMapping("/empresas")
    public List<String> getEmpresas() {
        return new ArrayList<>(dashboardService.getFuncionariosPorEmpresa().keySet());
    }

    /**
     * Lista de funcionários por CNPJ da empresa
     */
    @GetMapping("/funcionarios")
    public List<String> getFuncionarios(@RequestParam String empresa) {
        return dashboardService.getNomesFuncionariosByCnpjEmpresa(empresa);
    }

    /**
     * Horas trabalhadas por funcionário
     */
    @GetMapping("/horas-trabalhadas")
    public Map<String, Integer> getHorasTrabalhadas(
            @RequestParam String empresa,
            @RequestParam(required = false) String funcionario) {
        return dashboardService.getHorasTrabalhadas(empresa, funcionario);
    }
}