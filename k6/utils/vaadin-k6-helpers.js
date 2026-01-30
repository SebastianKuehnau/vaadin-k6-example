/**
 * Vaadin k6 Helper Functions
 *
 * Reusable utility functions for load testing Vaadin applications with k6.
 * Import these functions in your k6 test scripts to interact with Vaadin's
 * UIDL (UI Description Language) protocol.
 *
 * Usage:
 *   import { initPageLoad, vaadinRequest, vaadinUnloadRequest } from './vaadin-k6-helpers.js';
 *
 *   const BASE_URL = "http://localhost:8080";
 */

import http from "k6/http";
import { check } from "k6";

/**
 * Extracts the JSESSIONID from a response.
 * @param {Response} response - response
 * @returns {string|null} The JSESSIONID value or null if not found
 */
export function getJSessionId(response) {
    const cookieString = response.headers["Set-Cookie"];

    if (!cookieString) return null;
    const match = cookieString.match(/JSESSIONID=([^;]+)/);
    return match ? match[1] : null;
}

/**
 * Extracts the Vaadin CSRF security token from the HTML response.
 * @param {string} html - The HTML response body
 * @returns {string|null} The CSRF token or null if not found
 */
export function getVaadinSecurityKey(html) {
    const match = html.match(/["']Vaadin-Security-Key["']\s*:\s*["']([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})["']/);
    return match ? match[1] : null;
}

/**
 * Extracts the Vaadin Push ID from the HTML response.
 * @param {string} html - The HTML response body
 * @returns {string|null} The Push ID or null if not found
 */
export function getVaadinPushId(html) {
    const match = html.match(/["']Vaadin-Push-ID["']\s*:\s*["']([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})["']/);
    return match ? match[1] : null;
}

/**
 * Extracts the Vaadin UI ID from the HTML response.
 * @param {string} html - The HTML response body
 * @returns {number|null} The UI ID or null if not found
 */
export function getVaadinUiId(html) {
    const match = html.match(/["']v-uiId["']\s*:\s*(\d+)/);
    return match ? Number(match[1]) : null;
}