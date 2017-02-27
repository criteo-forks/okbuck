package com.uber.okbuck.core.dependency

class DirectDependency {
    final ExternalDependency direct
    final Set<ExternalDependency> children

    DirectDependency(ExternalDependency direct, Set<ExternalDependency> children) {
        this.direct = direct
        this.children = children
    }
}
