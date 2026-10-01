import { useEffect, useState } from "react";

interface DashboardData {
  totalWorkOrders: number;
  open: number;
  assigned: number;
  inProgress: number;
  completed: number;
  closed: number;
  cancelled: number;
  totalCustomers: number;
  totalSites: number;
  totalTechnicians: number;
}

interface Customer {
  id: number;
  name: string;
  email: string;
  phone: string | null;
  address: string | null;
  createdAt: string;
}

interface CustomerResponse {
  content: Customer[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

interface Site {
  id: number;
  name: string;
  address: string;
  phone: string;
  customerId: number;
  createdAt: string;
}

interface WorkOrder {
  id: number;
  code: string;
  title: string;
  description: string;
  priority: string;
  status: string;
  customerId: number;
  siteId: number;
  assignedTechnicianId: number | null;
  assignedTechnicianName: string | null;
  createdAt: string;
  updatedAt: string;
}

interface Part {
  id: number;
  name: string;
  partNumber: string;
  quantityAvailable: number;
  unitPrice: number;
  createdAt: string;
}

interface PartUsage {
  id: number;
  workOrderId: number;
  workOrderCode: string;
  partId: number;
  partName: string;
  partNumber: string;
  quantityUsed: number;
  usedAt: string;
}

interface TimeLog {
  id: number;
  workOrderId: number;
  workOrderCode: string;
  technicianId: number;
  technicianName: string;
  startTime: string;
  endTime: string | null;
  notes: string | null;
}

function App() {
  const [loggedIn, setLoggedIn] = useState(
    localStorage.getItem("token") !== null
  );

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [signupName, setSignupName] = useState("");
  const [signupEmail, setSignupEmail] = useState("");
  const [signupPassword, setSignupPassword] = useState("");
  const [signupConfirmPassword, setSignupConfirmPassword] = useState("");
  const [authMode, setAuthMode] = useState<"login" | "signup">("login");
  const [message, setMessage] = useState("");

  const [dashboard, setDashboard] =
    useState<DashboardData | null>(null);

  const [dashboardLoading, setDashboardLoading] =
    useState(false);

 const [page, setPage] = useState<
  | "dashboard"
  | "customers"
  | "sites"
  | "workOrders"
  | "workOrderDetails"
  | "createWorkOrder"
  | "parts"
  | "customerPortal"
>("dashboard");

  const [customers, setCustomers] =
    useState<Customer[]>([]);

  const [customerLoading, setCustomerLoading] =
    useState(false);

  const [customerSearch, setCustomerSearch] =
    useState("");

  const [sites, setSites] =
    useState<Site[]>([]);

    const [customerSites, setCustomerSites] =
  useState<Site[]>([]);

  const [selectedCustomer, setSelectedCustomer] =
    useState<Customer | null>(null);

  const [siteLoading, setSiteLoading] =
    useState(false);

  const [workOrders, setWorkOrders] =
    useState<WorkOrder[]>([]);

  const [customerWorkOrders, setCustomerWorkOrders] =
    useState<WorkOrder[]>([]);

  const [workOrderLoading, setWorkOrderLoading] =
    useState(false);

  const [selectedWorkOrder, setSelectedWorkOrder] =
    useState<WorkOrder | null>(null);

  const [createTitle, setCreateTitle] =
    useState("");

  const [createDescription, setCreateDescription] =
    useState("");

  const [createPriority, setCreatePriority] =
    useState("MEDIUM");

  
  const [createCustomerId, setCreateCustomerId] =
    useState("");

  const [createSiteId, setCreateSiteId] =
    useState("");

  const [createLoading, setCreateLoading] =
    useState(false);

      const [editMode, setEditMode] =
    useState(false);

  const [editTitle, setEditTitle] =
    useState("");

  const [editDescription, setEditDescription] =
    useState("");

  const [editPriority, setEditPriority] =
    useState("MEDIUM");

  const [editLoading, setEditLoading] =
    useState(false);

  const [parts, setParts] = useState<Part[]>([]);
  const [partLoading, setPartLoading] = useState(false);
  const [partName, setPartName] = useState("");
  const [partNumber, setPartNumber] = useState("");
  const [partQuantity, setPartQuantity] = useState("");
  const [partUnitPrice, setPartUnitPrice] = useState("");
  const [partSaving, setPartSaving] = useState(false);
  const [editingPartId, setEditingPartId] = useState<number | null>(null);

  const [partUsages, setPartUsages] = useState<PartUsage[]>([]);
  const [partUsageLoading, setPartUsageLoading] = useState(false);
  const [partUsageSaving, setPartUsageSaving] = useState(false);
  const [usagePartId, setUsagePartId] = useState("");
  const [usageQuantity, setUsageQuantity] = useState("1");

  const [timeLogs, setTimeLogs] = useState<TimeLog[]>([]);
  const [timeLogLoading, setTimeLogLoading] = useState(false);
  const [timeLogSaving, setTimeLogSaving] = useState(false);
  const [timeLogStart, setTimeLogStart] = useState("");
  const [timeLogEnd, setTimeLogEnd] = useState("");
  const [timeLogNotes, setTimeLogNotes] = useState("");

  const fetchDashboard = async () => {
    const token = localStorage.getItem("token");

    if (!token) return;

    setDashboardLoading(true);

    try {
      const response = await fetch(
        "http://localhost:8080/api/dashboard",
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        setMessage("Failed to load dashboard.");
        return;
      }

      const data = await response.json();
      setDashboard(data);
    } catch (error) {
      console.error(error);
      setMessage(
        "Cannot connect to dashboard server."
      );
    } finally {
      setDashboardLoading(false);
    }
  };

  const fetchCustomers = async () => {
    const token = localStorage.getItem("token");

    if (!token) return;

    setCustomerLoading(true);
    setMessage("");

    try {
      const response = await fetch(
        `http://localhost:8080/api/customers?search=${encodeURIComponent(
          customerSearch
        )}&page=0&size=10`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        setMessage("Failed to load customers.");
        return;
      }

      const data: CustomerResponse =
        await response.json();

      setCustomers(data.content);
    } catch (error) {
      console.error(error);
      setMessage(
        "Cannot connect to customer server."
      );
    } finally {
      setCustomerLoading(false);
    }
  };

  const fetchCustomerSites = async () => {
    const token = localStorage.getItem("token");

    if (!token) return;

    try {
      const customerResponse = await fetch(
        "http://localhost:8080/api/customers/me",
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!customerResponse.ok) {
        setMessage("Failed to load customer information.");
        return;
      }

      const customer: Customer =
        await customerResponse.json();

      const sitesResponse = await fetch(
        `http://localhost:8080/api/customers/${customer.id}/sites/my`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!sitesResponse.ok) {
        setMessage("Failed to load your sites.");
        return;
      }

      const data: Site[] =
        await sitesResponse.json();

      setCustomerSites(data);
    } catch (error) {
      console.error(error);
      setMessage("Cannot connect to site server.");
    }
  };

  const fetchCustomerWorkOrders = async () => {
    const token = localStorage.getItem("token");

    if (!token) return;

    try {
      const response = await fetch(
        "http://localhost:8080/api/work-orders/customer/me",
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      const data = await response.json().catch(() => []);

      if (!response.ok) {
        setMessage(
          data.message ||
            data.error ||
            "Failed to load your work orders."
        );
        return;
      }

      setCustomerWorkOrders(data as WorkOrder[]);
    } catch (error) {
      console.error(error);
      setMessage(
        "Cannot connect to work order server."
      );
    }
  };

  const fetchSites = async (
    customer: Customer
  ) => {
    const token = localStorage.getItem("token");

    if (!token) return;

    setSiteLoading(true);
    setMessage("");
    setSelectedCustomer(customer);

    try {
      const response = await fetch(
        `http://localhost:8080/api/customers/${customer.id}/sites`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        setMessage("Failed to load sites.");
        return;
      }

      const data: Site[] =
        await response.json();

      setSites(data);
      setPage("sites");
    } catch (error) {
      console.error(error);
      setMessage(
        "Cannot connect to site server."
      );
    } finally {
      setSiteLoading(false);
    }
  };
  const fetchWorkOrders = async () => {
    const token = localStorage.getItem("token");

    if (!token) return;

    setWorkOrderLoading(true);
    setMessage("");

    try {
      const response = await fetch(
        "http://localhost:8080/api/work-orders",
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        setMessage(
          "Failed to load work orders."
        );
        return;
      }

      const data: WorkOrder[] =
        await response.json();

      setWorkOrders(data);
    } catch (error) {
      console.error(error);
      setMessage(
        "Cannot connect to work order server."
      );
    } finally {
      setWorkOrderLoading(false);
    }
  };

  const fetchParts = async () => {
    const token = localStorage.getItem("token");

    if (!token) return;

    setPartLoading(true);
    setMessage("");

    try {
      const response = await fetch(
        "http://localhost:8080/api/parts",
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        const data = await response.json().catch(() => ({}));
        setMessage(
          data.message ||
            data.error ||
            "Failed to load parts."
        );
        return;
      }

      const data: Part[] = await response.json();
      setParts(data);
    } catch (error) {
      console.error(error);
      setMessage("Cannot connect to parts server.");
    } finally {
      setPartLoading(false);
    }
  };

  const fetchPartUsages = async (workOrderId: number) => {
    const token = localStorage.getItem("token");

    if (!token) return;

    setPartUsageLoading(true);

    try {
      const response = await fetch(
        `http://localhost:8080/api/work-orders/${workOrderId}/parts`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      const data = await response.json().catch(() => []);

      if (!response.ok) {
        setPartUsages([]);
        setMessage(
          data.message ||
            data.error ||
            "Failed to load parts used for this work order."
        );
        return;
      }

      setPartUsages(data as PartUsage[]);
    } catch (error) {
      console.error(error);
      setPartUsages([]);
      setMessage("Cannot connect to part usage server.");
    } finally {
      setPartUsageLoading(false);
    }
  };

  const fetchTimeLogs = async (workOrderId: number) => {
    const token = localStorage.getItem("token");

    if (!token) return;

    setTimeLogLoading(true);

    try {
      const response = await fetch(
        `http://localhost:8080/api/work-orders/${workOrderId}/time-logs`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      const data = await response.json().catch(() => []);

      if (!response.ok) {
        setTimeLogs([]);
        setMessage(
          data.message ||
            data.error ||
            "Failed to load time logs for this work order."
        );
        return;
      }

      setTimeLogs(data as TimeLog[]);
    } catch (error) {
      console.error(error);
      setTimeLogs([]);
      setMessage("Cannot connect to time log server.");
    } finally {
      setTimeLogLoading(false);
    }
  };

  const getLocalDateTimeInput = () => {
    const now = new Date();
    const offset = now.getTimezoneOffset();
    const local = new Date(now.getTime() - offset * 60000);
    return local.toISOString().slice(0, 16);
  };

  const resetTimeLogForm = () => {
    setTimeLogStart(getLocalDateTimeInput());
    setTimeLogEnd("");
    setTimeLogNotes("");
  };

  const addTimeLog = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("You are not logged in.");
      return;
    }

    if (!selectedWorkOrder) {
      setMessage("No work order selected.");
      return;
    }

    if (!selectedWorkOrder.assignedTechnicianId) {
      setMessage("A technician must be assigned before logging time.");
      return;
    }

    if (!timeLogStart) {
      setMessage("Start time is required.");
      return;
    }

    if (timeLogEnd && new Date(timeLogEnd).getTime() < new Date(timeLogStart).getTime()) {
      setMessage("End time cannot be before start time.");
      return;
    }

    setTimeLogSaving(true);
    setMessage("");

    try {
      const response = await fetch(
        `http://localhost:8080/api/work-orders/${selectedWorkOrder.id}/time-logs`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            technicianId: selectedWorkOrder.assignedTechnicianId,
            startTime: timeLogStart,
            endTime: timeLogEnd || null,
            notes: timeLogNotes.trim() || null,
          }),
        }
      );

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        setMessage(
          data.message ||
            data.error ||
            "Failed to add time log."
        );
        return;
      }

      await fetchTimeLogs(selectedWorkOrder.id);
      resetTimeLogForm();
      setMessage("Time log added successfully.");
    } catch (error) {
      console.error(error);
      setMessage("Cannot connect to time log server.");
    } finally {
      setTimeLogSaving(false);
    }
  };

  const addPartUsage = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("You are not logged in.");
      return;
    }

    if (!selectedWorkOrder) {
      setMessage("No work order selected.");
      return;
    }

    if (!usagePartId) {
      setMessage("Please select a part.");
      return;
    }

    const quantity = Number(usageQuantity);

    if (!Number.isInteger(quantity) || quantity < 1) {
      setMessage("Quantity used must be at least 1.");
      return;
    }

    const selectedPart = parts.find(
      (part) => part.id === Number(usagePartId)
    );

    if (!selectedPart) {
      setMessage("Selected part was not found.");
      return;
    }

    if (quantity > selectedPart.quantityAvailable) {
      setMessage(
        `Only ${selectedPart.quantityAvailable} unit(s) of ${selectedPart.name} are available.`
      );
      return;
    }

    setPartUsageSaving(true);
    setMessage("");

    try {
      const response = await fetch(
        `http://localhost:8080/api/work-orders/${selectedWorkOrder.id}/parts`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            partId: Number(usagePartId),
            quantityUsed: quantity,
          }),
        }
      );

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        setMessage(
          data.message ||
            data.error ||
            "Failed to add part usage."
        );
        return;
      }

      setUsagePartId("");
      setUsageQuantity("1");
      await fetchPartUsages(selectedWorkOrder.id);
      await fetchParts();
      setMessage(
        `${selectedPart.name} usage added successfully.`
      );
    } catch (error) {
      console.error(error);
      setMessage("Cannot connect to part usage server.");
    } finally {
      setPartUsageSaving(false);
    }
  };

  const openParts = () => {
    setPage("parts");
    setMessage("");
  };

  const resetPartForm = () => {
    setPartName("");
    setPartNumber("");
    setPartQuantity("");
    setPartUnitPrice("");
    setEditingPartId(null);
  };

  const startEditPart = (part: Part) => {
    setEditingPartId(part.id);
    setPartName(part.name);
    setPartNumber(part.partNumber);
    setPartQuantity(String(part.quantityAvailable));
    setPartUnitPrice(String(part.unitPrice));
    setMessage("");
  };

  const savePart = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("You are not logged in.");
      return;
    }

    if (!partName.trim()) {
      setMessage("Part name is required.");
      return;
    }

    if (!partNumber.trim()) {
      setMessage("Part number is required.");
      return;
    }

    if (
      partQuantity.trim() === "" ||
      Number(partQuantity) < 0 ||
      !Number.isInteger(Number(partQuantity))
    ) {
      setMessage("Quantity must be a non-negative whole number.");
      return;
    }

    if (
      partUnitPrice.trim() === "" ||
      Number(partUnitPrice) < 0 ||
      Number.isNaN(Number(partUnitPrice))
    ) {
      setMessage("Unit price must be a non-negative number.");
      return;
    }

    setPartSaving(true);
    setMessage("");

    const payload = {
      name: partName.trim(),
      partNumber: partNumber.trim(),
      quantityAvailable: Number(partQuantity),
      unitPrice: Number(partUnitPrice),
    };

    try {
      const url = editingPartId
        ? `http://localhost:8080/api/parts/${editingPartId}`
        : "http://localhost:8080/api/parts";

      const response = await fetch(url, {
        method: editingPartId ? "PUT" : "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        setMessage(
          data.message ||
            data.error ||
            "Failed to save part."
        );
        return;
      }

      const action = editingPartId ? "updated" : "created";
      resetPartForm();
      await fetchParts();
      setMessage(`Part ${data.name || payload.name} ${action} successfully.`);
    } catch (error) {
      console.error(error);
      setMessage("Cannot connect to parts server.");
    } finally {
      setPartSaving(false);
    }
  };

  const deletePart = async (part: Part) => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("You are not logged in.");
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete ${part.name} (${part.partNumber})? This action cannot be undone.`
    );

    if (!confirmed) return;

    setMessage("Deleting part...");

    try {
      const response = await fetch(
        `http://localhost:8080/api/parts/${part.id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        setMessage(
          data.message ||
            data.error ||
            "Failed to delete part."
        );
        return;
      }

      if (editingPartId === part.id) {
        resetPartForm();
      }

      await fetchParts();
      setMessage(`Part ${part.name} deleted successfully.`);
    } catch (error) {
      console.error(error);
      setMessage("Cannot connect to parts server.");
    }
  };

  useEffect(() => {
  const role = localStorage.getItem("role");

  if (
    loggedIn &&
    (role === "MANAGER" || role === "DISPATCHER")
  ) {
    fetchDashboard();
  }
}, [loggedIn]);

  useEffect(() => {
    if (
      loggedIn &&
      page === "customers"
    ) {
      fetchCustomers();
    }
  }, [page]);

  useEffect(() => {
    const role = localStorage.getItem("role");

    if (
      loggedIn &&
      page === "workOrders" &&
      role !== "CUSTOMER"
    ) {
      fetchWorkOrders();
    }
  }, [page]);

  useEffect(() => {
    if (
      loggedIn &&
      page === "parts"
    ) {
      fetchParts();
    }
  }, [page]);

  useEffect(() => {
    if (
      loggedIn &&
      page === "workOrderDetails" &&
      selectedWorkOrder
    ) {
      fetchPartUsages(selectedWorkOrder.id);
      fetchTimeLogs(selectedWorkOrder.id);
      fetchParts();
    }
  }, [page, selectedWorkOrder?.id]);

  const handleLogin = async (
    e: React.FormEvent
  ) => {
    e.preventDefault();

    setMessage("Logging in...");

    try {
      const response = await fetch(
        "http://localhost:8080/api/auth/login",
        {
          method: "POST",
          headers: {
            "Content-Type":
              "application/json",
          },
          body: JSON.stringify({
            email,
            password,
          }),
        }
      );

      const data =
        await response.json();

      if (!response.ok) {
        setMessage(
          data.message || "Login failed"
        );
        return;
      }

      localStorage.setItem(
  "token",
  data.token
);

localStorage.setItem(
  "role",
  data.role
);

setLoggedIn(true);
setMessage("");
    } catch (error) {
      console.error(error);
      setMessage(
        "Cannot connect to server."
      );
    }
  };

  const handleRegister = async (
    e: React.FormEvent
  ) => {
    e.preventDefault();

    if (!signupName.trim()) {
      setMessage("Name is required.");
      return;
    }

    if (!signupEmail.trim()) {
      setMessage("Email is required.");
      return;
    }

    if (signupPassword.length < 6) {
      setMessage("Password must be at least 6 characters.");
      return;
    }

    if (signupPassword !== signupConfirmPassword) {
      setMessage("Passwords do not match.");
      return;
    }

    setMessage("Creating your account...");

    try {
      const response = await fetch(
        "http://localhost:8080/api/auth/register",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            name: signupName.trim(),
            email: signupEmail.trim(),
            password: signupPassword,
          }),
        }
      );

      let data: {
        message?: string;
        error?: string;
      } = {};

      try {
        data = await response.json();
      } catch {
        // Response may not contain JSON.
      }

      if (!response.ok) {
        setMessage(
          data.message ||
            data.error ||
            "Registration failed."
        );
        return;
      }

      setEmail(signupEmail.trim());
      setPassword("");
      setSignupName("");
      setSignupEmail("");
      setSignupPassword("");
      setSignupConfirmPassword("");
      setAuthMode("login");
      setMessage(
        "Account created successfully. Please login."
      );
    } catch (error) {
      console.error(error);
      setMessage("Cannot connect to server.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");

    setLoggedIn(false);
    setEmail("");
    setPassword("");
    setDashboard(null);
    setCustomers([]);
    setSites([]);
    setCustomerSites([]);
    setCustomerWorkOrders([]);
    setWorkOrders([]);
    setParts([]);
    setPartUsages([]);
    setUsagePartId("");
    setUsageQuantity("1");
    setTimeLogs([]);
    resetTimeLogForm();
    resetPartForm();
    setSelectedCustomer(null);
    setSelectedWorkOrder(null);
    setPage("dashboard");
    setMessage("");
  };

  const openCustomers = () => {
    setPage("customers");
    setCustomerSearch("");
    setMessage("");
  };

  const openWorkOrders = () => {
    setPage("workOrders");
    setMessage("");
  };

    const openWorkOrderDetails = (
    workOrder: WorkOrder
  ) => {
    setSelectedWorkOrder(workOrder);

    setEditTitle(workOrder.title);
    setEditDescription(workOrder.description || "");
    setEditPriority(workOrder.priority);

    setEditMode(false);
    setPartUsages([]);
    setUsagePartId("");
    setUsageQuantity("1");
    setTimeLogs([]);
    resetTimeLogForm();
    setMessage("");

    setPage("workOrderDetails");
  };

  const openCreateWorkOrder = () => {
    setCreateTitle("");
    setCreateDescription("");
    setCreatePriority("MEDIUM");
    setCreateCustomerId("");
    setCreateSiteId("");
    setMessage("");
    setPage("createWorkOrder");
  };

  const backToDashboard = () => {
    setPage("dashboard");
    setMessage("");
  };

  const backToCustomers = () => {
    setPage("customers");
    setSites([]);
    setSelectedCustomer(null);
    setMessage("");
  };

  const backToWorkOrders = () => {
    setPage("workOrders");
    setSelectedWorkOrder(null);
    setPartUsages([]);
    setUsagePartId("");
    setUsageQuantity("1");
    setTimeLogs([]);
    resetTimeLogForm();
    setMessage("");
  };

  const createWorkOrder = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("You are not logged in.");
      return;
    }

    if (!createTitle.trim()) {
      setMessage("Title is required.");
      return;
    }

    if (!createCustomerId.trim()) {
      setMessage("Customer ID is required.");
      return;
    }

    if (!createSiteId.trim()) {
      setMessage("Site ID is required.");
      return;
    }

    setCreateLoading(true);
    setMessage("");

    try {
      const response = await fetch(
        "http://localhost:8080/api/work-orders",
        {
          method: "POST",
          headers: {
            Authorization:
              `Bearer ${token}`,
            "Content-Type":
              "application/json",
          },
          body: JSON.stringify({
            title: createTitle,
            description: createDescription,
            priority: createPriority,
            customerId:
              Number(createCustomerId),
            siteId:
              Number(createSiteId),
          }),
        }
      );

      const data =
        await response.json();

      if (!response.ok) {
        setMessage(
          data.message ||
            data.error ||
            "Failed to create work order."
        );
        return;
      }

      setMessage(
        `Work order ${data.code} created successfully.`
      );

      await fetchWorkOrders();
      setPage("workOrders");
    } catch (error) {
      console.error(error);
      setMessage(
        "Cannot connect to work order server."
      );
    } finally {
      setCreateLoading(false);
    }
  };

  const updateWorkOrder = async () => {
    const token = localStorage.getItem("token");
    if (!token) { setMessage("You are not logged in."); return; }
    if (!selectedWorkOrder) { setMessage("No work order selected."); return; }
    if (!editTitle.trim()) { setMessage("Title is required."); return; }
    setEditLoading(true); setMessage("");
    try {
      const response = await fetch(`http://localhost:8080/api/work-orders/${selectedWorkOrder.id}`, {
        method: "PUT",
        headers: { Authorization: `Bearer ${token}`, "Content-Type": "application/json" },
        body: JSON.stringify({ title: editTitle, description: editDescription, priority: editPriority, customerId: selectedWorkOrder.customerId, siteId: selectedWorkOrder.siteId })
      });
      const data = await response.json();
      if (!response.ok) { setMessage(data.message || data.error || "Failed to update work order."); return; }
      setSelectedWorkOrder(data); setEditTitle(data.title); setEditDescription(data.description || ""); setEditPriority(data.priority); setEditMode(false);
      await fetchWorkOrders();
      setMessage(`Work order ${data.code} updated successfully.`);
    } catch (error) { console.error(error); setMessage("Cannot connect to work order server."); }
    finally { setEditLoading(false); }
  };
    const deleteWorkOrder = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("You are not logged in.");
      return;
    }

    if (!selectedWorkOrder) {
      setMessage("No work order selected.");
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete ${selectedWorkOrder.code}? This action cannot be undone.`
    );

    if (!confirmed) {
      return;
    }

    setMessage("Deleting work order...");

    try {
      const response = await fetch(
        `http://localhost:8080/api/work-orders/${selectedWorkOrder.id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        let data: { message?: string; error?: string } = {};

        try {
          data = await response.json();
        } catch {
          // Response may not contain JSON.
        }

        setMessage(
          data.message ||
            data.error ||
            "Failed to delete work order."
        );
        return;
      }

      setSelectedWorkOrder(null);
      setEditMode(false);
      setMessage(
        `Work order ${selectedWorkOrder.code} deleted successfully.`
      );

      await fetchWorkOrders();
      setPage("workOrders");
    } catch (error) {
      console.error(error);
      setMessage(
        "Cannot connect to work order server."
      );
    }
  };

  if (!loggedIn) {
    return (
      <div style={styles.page}>

        <div style={styles.loginCard}>

          <div style={styles.logoSection}>

            <div style={styles.logo}>
              K
            </div>

            <h1 style={styles.title}>
              PROJECT KEYSTONE
            </h1>

            <p style={styles.subtitle}>
              Field Service Management Platform
            </p>

          </div>

          {authMode === "login" ? (
            <form
              onSubmit={handleLogin}
              style={styles.form}
            >

              <h2 style={styles.loginTitle}>
                Welcome Back
              </h2>

              <p style={styles.loginSubtitle}>
                Sign in to your Keystone account
              </p>

              <label style={styles.label}>
                Email
              </label>

              <input
                type="email"
                placeholder="Enter your email"
                value={email}
                onChange={(e) =>
                  setEmail(e.target.value)
                }
                style={styles.input}
                required
              />

              <label style={styles.label}>
                Password
              </label>

              <input
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) =>
                  setPassword(e.target.value)
                }
                style={styles.input}
                required
              />

              <button
                type="submit"
                style={styles.button}
              >
                Login
              </button>

              <button
                type="button"
                style={styles.secondaryButton}
                onClick={() => {
                  setAuthMode("signup");
                  setMessage("");
                }}
              >
                Sign Up
              </button>

              {message && (
                <p style={styles.message}>
                  {message}
                </p>
              )}

            </form>
          ) : (
            <form
              onSubmit={handleRegister}
              style={styles.form}
            >

              <h2 style={styles.loginTitle}>
                Create Account
              </h2>

              <p style={styles.loginSubtitle}>
                Register as a Keystone customer
              </p>

              <label style={styles.label}>
                Full Name
              </label>

              <input
                type="text"
                placeholder="Enter your full name"
                value={signupName}
                onChange={(e) =>
                  setSignupName(e.target.value)
                }
                style={styles.input}
                required
              />

              <label style={styles.label}>
                Email
              </label>

              <input
                type="email"
                placeholder="Enter your email"
                value={signupEmail}
                onChange={(e) =>
                  setSignupEmail(e.target.value)
                }
                style={styles.input}
                required
              />

              <label style={styles.label}>
                Password
              </label>

              <input
                type="password"
                placeholder="Create a password"
                value={signupPassword}
                onChange={(e) =>
                  setSignupPassword(e.target.value)
                }
                style={styles.input}
                required
                minLength={6}
              />

              <label style={styles.label}>
                Confirm Password
              </label>

              <input
                type="password"
                placeholder="Confirm your password"
                value={signupConfirmPassword}
                onChange={(e) =>
                  setSignupConfirmPassword(e.target.value)
                }
                style={styles.input}
                required
                minLength={6}
              />

              <button
                type="submit"
                style={styles.button}
              >
                Create Account
              </button>

              <button
                type="button"
                style={styles.secondaryButton}
                onClick={() => {
                  setAuthMode("login");
                  setMessage("");
                }}
              >
                ← Back to Login
              </button>

              {message && (
                <p style={styles.message}>
                  {message}
                </p>
              )}

            </form>
          )}

          <p style={styles.footer}>
            © 2026 Project Keystone
          </p>

        </div>

      </div>
    );
  }

  return (
    <div style={styles.dashboard}>

      <header style={styles.header}>

        <div>

          <h1 style={styles.headerTitle}>
            PROJECT KEYSTONE
          </h1>

          <p style={styles.headerSubtitle}>
            Field Service Management Platform
          </p>

        </div>

        <button
          onClick={handleLogout}
          style={styles.logoutButton}
        >
          Logout
        </button>

      </header>

    {localStorage.getItem("role") === "CUSTOMER" &&
      page === "dashboard" && (
        <main style={styles.main}>
          <h2 style={styles.welcome}>
            Welcome to Your Keystone Portal 👋
          </h2>

          <p style={styles.loading}>
            Customer Portal
          </p>

          <div style={styles.cards}>
            <div
              style={{
                ...styles.card,
                cursor: "pointer",
              }}
              onClick={async () => {
                await fetchCustomerSites();
                setPage("sites");
              }}
            >
              <h3>My Sites</h3>
              <p>
                View the sites registered under your account.
              </p>
            </div>

            <div
              style={{
                ...styles.card,
                cursor: "pointer",
              }}
              onClick={async () => {
                await fetchCustomerWorkOrders();
                setPage("workOrders");
              }}
            >
              <h3>My Work Orders</h3>
              <p>
                View your service requests and work-order status.
              </p>
            </div>

            <div style={styles.card}>
              <h3>Service History</h3>
              <p>
                Track the history of your service activities.
              </p>
            </div>
          </div>
        </main>
      )}


{page === "dashboard" && localStorage.getItem("role") !== "CUSTOMER" && (
        <main style={styles.main}>

          <h2 style={styles.welcome}>
            Welcome to Keystone 👋
          </h2>

          {dashboardLoading && (
            <p style={styles.loading}>
              Loading dashboard data...
            </p>
          )}

          {message && (
            <p style={styles.message}>
              {message}
            </p>
          )}

          {dashboard && (
            <>

              <div style={styles.cards}>

                <div
                  style={{
                    ...styles.card,
                    cursor: "pointer",
                  }}
                  onClick={openWorkOrders}
                >

                  <h3>
                    Total Work Orders
                  </h3>

                  <p style={styles.number}>
                    {dashboard.totalWorkOrders}
                  </p>

                  <p style={styles.cardLink}>
                    View Work Orders →
                  </p>

                </div>

                <div
                  style={{
                    ...styles.card,
                    cursor: "pointer",
                  }}
                  onClick={openCustomers}
                >

                  <h3>
                    Total Customers
                  </h3>

                  <p style={styles.number}>
                    {dashboard.totalCustomers}
                  </p>

                  <p style={styles.cardLink}>
                    View Customers →
                  </p>

                </div>

                <div
                  style={{
                    ...styles.card,
                    cursor: "pointer",
                  }}
                  onClick={openParts}
                >
                  <h3>
                    Parts
                  </h3>
                  <p style={styles.number}>
                    {parts.length}
                  </p>
                  <p style={styles.cardLink}>
                    View Parts →
                  </p>
                </div>

                <div style={styles.card}>

                  <h3>
                    Total Sites
                  </h3>

                  <p style={styles.number}>
                    {dashboard.totalSites}
                  </p>

                </div>

                <div style={styles.card}>

                  <h3>
                    Total Technicians
                  </h3>

                  <p style={styles.number}>
                    {dashboard.totalTechnicians}
                  </p>

                </div>

              </div>

              <h2 style={styles.sectionTitle}>
                Work Order Status
              </h2>

              <div style={styles.cards}>

                <div style={styles.statusCard}>
                  <h3>Open</h3>

                  <p style={styles.number}>
                    {dashboard.open}
                  </p>
                </div>

                <div style={styles.statusCard}>
                  <h3>Assigned</h3>

                  <p style={styles.number}>
                    {dashboard.assigned}
                  </p>
                </div>

                <div style={styles.statusCard}>
                  <h3>In Progress</h3>

                  <p style={styles.number}>
                    {dashboard.inProgress}
                  </p>
                </div>

                <div style={styles.statusCard}>
                  <h3>Completed</h3>

                  <p style={styles.number}>
                    {dashboard.completed}
                  </p>
                </div>

                <div style={styles.statusCard}>
                  <h3>Closed</h3>

                  <p style={styles.number}>
                    {dashboard.closed}
                  </p>
                </div>

                <div style={styles.statusCard}>
                  <h3>Cancelled</h3>

                  <p style={styles.number}>
                    {dashboard.cancelled}
                  </p>
                </div>

              </div>

            </>
          )}

        </main>
      )}

      {page === "customers" && (
        <main style={styles.main}>

          <button
            onClick={backToDashboard}
            style={styles.backButton}
          >
            ← Back to Dashboard
          </button>

          <h2 style={styles.welcome}>
            Customers
          </h2>

          <div style={styles.searchContainer}>

            <input
              type="text"
              placeholder="Search customers..."
              value={customerSearch}
              onChange={(e) =>
                setCustomerSearch(e.target.value)
              }
              style={styles.searchInput}
            />

            <button
              onClick={fetchCustomers}
              style={styles.searchButton}
            >
              Search
            </button>

            <button
              onClick={fetchCustomers}
              style={styles.refreshButton}
            >
              Refresh
            </button>

          </div>

          {customerLoading && (
            <p style={styles.loading}>
              Loading customers...
            </p>
          )}

          {message && (
            <p style={styles.message}>
              {message}
            </p>
          )}

          {!customerLoading &&
            customers.length === 0 && (
              <div style={styles.emptyCard}>

                <h3>
                  No customers found
                </h3>

                <p>
                  There are no customers
                  matching your search.
                </p>

              </div>
            )}

          {customers.length > 0 && (
            <div style={styles.customerTableContainer}>

              <table style={styles.table}>

                <thead>

                  <tr>

                    <th style={styles.th}>
                      ID
                    </th>

                    <th style={styles.th}>
                      Name
                    </th>

                    <th style={styles.th}>
                      Email
                    </th>

                    <th style={styles.th}>
                      Phone
                    </th>

                    <th style={styles.th}>
                      Address
                    </th>

                    <th style={styles.th}>
                      Action
                    </th>

                  </tr>

                </thead>

                <tbody>

                  {customers.map((customer) => (
                    <tr key={customer.id}>

                      <td style={styles.td}>
                        {customer.id}
                      </td>

                      <td style={styles.td}>
                        {customer.name}
                      </td>

                      <td style={styles.td}>
                        {customer.email}
                      </td>

                      <td style={styles.td}>
                        {customer.phone ||
                          "Not provided"}
                      </td>

                      <td style={styles.td}>
                        {customer.address ||
                          "Not provided"}
                      </td>

                      <td style={styles.td}>

                        <button
                          onClick={() =>
                            fetchSites(customer)
                          }
                          style={styles.siteButton}
                        >
                          View Sites
                        </button>

                      </td>

                    </tr>
                  ))}

                </tbody>

              </table>

            </div>
          )}

        </main>
      )}

      
      {page === "sites" &&
        localStorage.getItem("role") === "CUSTOMER" && (
          <main style={styles.main}>

            <button
              onClick={() => {
                setPage("dashboard");
                setMessage("");
              }}
              style={styles.backButton}
            >
              ← Back to Portal
            </button>

            <h2 style={styles.welcome}>
              My Sites
            </h2>

            {message && (
              <p style={styles.message}>
                {message}
              </p>
            )}

            {customerSites.length === 0 ? (
              <div style={styles.emptyCard}>
                <h3>No sites found</h3>
                <p>
                  No sites are currently registered under your account.
                </p>
              </div>
            ) : (
              <div style={styles.cards}>
                {customerSites.map((site) => (
                  <div
                    key={site.id}
                    style={styles.siteCard}
                  >
                    <h3>{site.name}</h3>

                    <p>
                      <strong>Address:</strong>{" "}
                      {site.address}
                    </p>

                    <p>
                      <strong>Phone:</strong>{" "}
                      {site.phone}
                    </p>

                    <p style={styles.siteId}>
                      Site ID: {site.id}
                    </p>
                  </div>
                ))}
              </div>
            )}

          </main>
        )}

      {page === "sites" &&
  localStorage.getItem("role") !== "CUSTOMER" && (
        <main style={styles.main}>

          <button
            onClick={backToCustomers}
            style={styles.backButton}
          >
            ← Back to Customers
          </button>

          <h2 style={styles.welcome}>
            Sites
          </h2>

          {selectedCustomer && (
            <div style={styles.customerInfo}>

              <h3>
                {selectedCustomer.name}
              </h3>

              <p>
                {selectedCustomer.email}
              </p>

            </div>
          )}

          {siteLoading && (
            <p style={styles.loading}>
              Loading sites...
            </p>
          )}

          {message && (
            <p style={styles.message}>
              {message}
            </p>
          )}

          {!siteLoading &&
            sites.length === 0 && (
              <div style={styles.emptyCard}>

                <h3>
                  No sites found
                </h3>

                <p>
                  This customer does not
                  have any sites yet.
                </p>

              </div>
            )}

          {sites.length > 0 && (
            <div style={styles.cards}>

              {sites.map((site) => (
                <div
                  key={site.id}
                  style={styles.siteCard}
                >

                  <h3>
                    {site.name}
                  </h3>

                  <p>
                    <strong>
                      Address:
                    </strong>{" "}
                    {site.address}
                  </p>

                  <p>
                    <strong>
                      Phone:
                    </strong>{" "}
                    {site.phone}
                  </p>

                  <p style={styles.siteId}>
                    Site ID: {site.id}
                  </p>

                </div>
              ))}

            </div>
          )}

        </main>
      )}

      {page === "workOrders" &&
        localStorage.getItem("role") === "CUSTOMER" && (
        <main style={styles.main}>
          <button
            onClick={() => {
              setPage("dashboard");
              setMessage("");
            }}
            style={styles.backButton}
          >
            ← Back to Portal
          </button>

          <h2 style={styles.welcome}>
            My Work Orders
          </h2>

          {message && (
            <p style={styles.message}>
              {message}
            </p>
          )}

          {customerWorkOrders.length === 0 ? (
            <div style={styles.emptyCard}>
              <h3>No work orders found</h3>
              <p>
                You currently have no work orders.
              </p>
            </div>
          ) : (
            <div style={styles.customerTableContainer}>
              <table style={styles.table}>
                <thead>
                  <tr>
                    <th style={styles.th}>Code</th>
                    <th style={styles.th}>Title</th>
                    <th style={styles.th}>Priority</th>
                    <th style={styles.th}>Status</th>
                    <th style={styles.th}>Site ID</th>
                    <th style={styles.th}>Technician</th>
                  </tr>
                </thead>

                <tbody>
                  {customerWorkOrders.map((workOrder) => (
                    <tr key={workOrder.id}>
                      <td style={styles.td}>
                        <strong>{workOrder.code}</strong>
                      </td>

                      <td style={styles.td}>
                        {workOrder.title}
                      </td>

                      <td style={styles.td}>
                        <span style={styles.priorityBadge}>
                          {workOrder.priority}
                        </span>
                      </td>

                      <td style={styles.td}>
                        <span style={styles.statusBadge}>
                          {workOrder.status}
                        </span>
                      </td>

                      <td style={styles.td}>
                        {workOrder.siteId}
                      </td>

                      <td style={styles.td}>
                        {workOrder.assignedTechnicianName ||
                          "Not assigned"}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </main>
      )}

      {page === "workOrders" &&
        localStorage.getItem("role") !== "CUSTOMER" && (
        <main style={styles.main}>

          <div style={styles.pageHeader}>

            <button
              onClick={backToDashboard}
              style={styles.backButton}
            >
              ← Back to Dashboard
            </button>

            <h2 style={styles.welcome}>
              Work Orders
            </h2>

          </div>

          <div style={styles.workOrderActions}>

            <button
              onClick={openCreateWorkOrder}
              style={styles.createButton}
            >
              + Create Work Order
            </button>

            <button
              onClick={fetchWorkOrders}
              style={styles.refreshButton}
            >
              Refresh Work Orders
            </button>

          </div>

          {workOrderLoading && (
            <p style={styles.loading}>
              Loading work orders...
            </p>
          )}

          {message && (
            <p style={styles.message}>
              {message}
            </p>
          )}

          {!workOrderLoading &&
            workOrders.length === 0 && (
              <div style={styles.emptyCard}>

                <h3>
                  No work orders found
                </h3>

                <p>
                  There are currently no work
                  orders in the system.
                </p>

              </div>
            )}

          {workOrders.length > 0 && (
            <div style={styles.customerTableContainer}>

              <table style={styles.table}>

                <thead>

                  <tr>

                    <th style={styles.th}>
                      Code
                    </th>

                    <th style={styles.th}>
                      Title
                    </th>

                    <th style={styles.th}>
                      Priority
                    </th>

                    <th style={styles.th}>
                      Status
                    </th>

                    <th style={styles.th}>
                      Customer ID
                    </th>

                    <th style={styles.th}>
                      Site ID
                    </th>

                    <th style={styles.th}>
                      Technician
                    </th>

                    <th style={styles.th}>
                      Action
                    </th>

                  </tr>

                </thead>

                <tbody>

                  {workOrders.map((workOrder) => (
                    <tr key={workOrder.id}>

                      <td style={styles.td}>
                        <strong>
                          {workOrder.code}
                        </strong>
                      </td>

                      <td style={styles.td}>
                        {workOrder.title}
                      </td>

                      <td style={styles.td}>

                        <span
                          style={styles.priorityBadge}
                        >
                          {workOrder.priority}
                        </span>

                      </td>

                      <td style={styles.td}>

                        <span
                          style={styles.statusBadge}
                        >
                          {workOrder.status}
                        </span>

                      </td>

                      <td style={styles.td}>
                        {workOrder.customerId}
                      </td>

                      <td style={styles.td}>
                        {workOrder.siteId}
                      </td>

                      <td style={styles.td}>
                        {workOrder.assignedTechnicianName ||
                          "Not assigned"}
                      </td>

                      <td style={styles.td}>

                        <button
                          onClick={() =>
                            openWorkOrderDetails(
                              workOrder
                            )
                          }
                          style={styles.viewButton}
                        >
                          View Details
                        </button>

                      </td>

                    </tr>
                  ))}

                </tbody>

              </table>

            </div>
          )}

        </main>
      )}
            {page === "workOrderDetails" && selectedWorkOrder && (
        <main style={styles.main}>
          <button onClick={backToWorkOrders} style={styles.backButton}>← Back to Work Orders</button>
          <h2 style={styles.welcome}>Work Order Details</h2>
          {message && <p style={styles.message}>{message}</p>}
          <div style={styles.detailsCard}>
            <div style={styles.detailsHeader}>
              <div><p style={styles.code}>{selectedWorkOrder.code}</p><h2>{editMode ? "Edit Work Order" : selectedWorkOrder.title}</h2></div>
              <span style={styles.statusBadge}>{selectedWorkOrder.status}</span>
            </div>
            {editMode ? (
              <div style={styles.editForm}>
                <div style={styles.formGroup}><label style={styles.label}>Title *</label><input type="text" value={editTitle} onChange={e => setEditTitle(e.target.value)} style={styles.input} maxLength={150} /></div>
                <div style={styles.formGroup}><label style={styles.label}>Description</label><textarea value={editDescription} onChange={e => setEditDescription(e.target.value)} style={styles.textarea} rows={5} maxLength={1000} /></div>
                <div style={styles.formGroup}><label style={styles.label}>Priority *</label><select value={editPriority} onChange={e => setEditPriority(e.target.value)} style={styles.input}><option value="LOW">LOW</option><option value="MEDIUM">MEDIUM</option><option value="HIGH">HIGH</option><option value="URGENT">URGENT</option></select></div>
                <div style={styles.editInfoGrid}><div style={styles.detailItem}><span style={styles.detailLabel}>Customer ID</span><strong>{selectedWorkOrder.customerId}</strong></div><div style={styles.detailItem}><span style={styles.detailLabel}>Site ID</span><strong>{selectedWorkOrder.siteId}</strong></div></div>
                <div style={styles.editActions}><button onClick={updateWorkOrder} disabled={editLoading} style={{...styles.createButton, opacity: editLoading ? 0.6 : 1}}>{editLoading ? "Saving..." : "Save Changes"}</button><button onClick={() => { setEditMode(false); setEditTitle(selectedWorkOrder.title); setEditDescription(selectedWorkOrder.description || ""); setEditPriority(selectedWorkOrder.priority); setMessage(""); }} disabled={editLoading} style={styles.cancelButton}>Cancel</button></div>
              </div>
            ) : (
              <>
                <div style={styles.detailsActions}>
                  <button
                    onClick={() => {
                      setEditTitle(selectedWorkOrder.title);
                      setEditDescription(selectedWorkOrder.description || "");
                      setEditPriority(selectedWorkOrder.priority);
                      setEditMode(true);
                      setMessage("");
                    }}
                    style={styles.editButton}
                  >
                    ✏️ Edit Work Order
                  </button>

                  <button
                    onClick={deleteWorkOrder}
                    style={styles.deleteButton}
                  >
                    🗑️ Delete Work Order
                  </button>
                </div>
                <div style={styles.detailsGrid}>
                  <div style={styles.detailItem}><span style={styles.detailLabel}>Priority</span><strong>{selectedWorkOrder.priority}</strong></div>
                  <div style={styles.detailItem}><span style={styles.detailLabel}>Customer ID</span><strong>{selectedWorkOrder.customerId}</strong></div>
                  <div style={styles.detailItem}><span style={styles.detailLabel}>Site ID</span><strong>{selectedWorkOrder.siteId}</strong></div>
                  <div style={styles.detailItem}><span style={styles.detailLabel}>Technician</span><strong>{selectedWorkOrder.assignedTechnicianName || "Not assigned"}</strong></div>
                  <div style={styles.detailItem}><span style={styles.detailLabel}>Created</span><strong>{new Date(selectedWorkOrder.createdAt).toLocaleString()}</strong></div>
                  <div style={styles.detailItem}><span style={styles.detailLabel}>Last Updated</span><strong>{new Date(selectedWorkOrder.updatedAt).toLocaleString()}</strong></div>
                </div>
                <div style={styles.descriptionBox}><h3>Description</h3><p>{selectedWorkOrder.description || "No description provided."}</p></div>
              </>
            )}
          </div>

          <div style={styles.partUsageCard}>
            <div style={styles.partUsageHeader}>
              <div>
                <h3 style={{ margin: 0, color: "#111827" }}>Parts Used</h3>
                <p style={styles.partUsageSubtitle}>
                  Add parts consumed while completing this work order. Stock is reduced automatically.
                </p>
              </div>
            </div>

            <div style={styles.partUsageForm}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Part *</label>
                <select
                  value={usagePartId}
                  onChange={(e) => setUsagePartId(e.target.value)}
                  style={styles.input}
                  disabled={partUsageSaving}
                >
                  <option value="">Select a part</option>
                  {parts
                    .filter((part) => part.quantityAvailable > 0)
                    .map((part) => (
                      <option key={part.id} value={part.id}>
                        {part.name} ({part.partNumber}) — Stock: {part.quantityAvailable}
                      </option>
                    ))}
                </select>
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>Quantity Used *</label>
                <input
                  type="number"
                  min="1"
                  step="1"
                  value={usageQuantity}
                  onChange={(e) => setUsageQuantity(e.target.value)}
                  style={styles.input}
                  disabled={partUsageSaving}
                />
              </div>

              <button
                onClick={addPartUsage}
                disabled={partUsageSaving || partUsageLoading}
                style={{
                  ...styles.createButton,
                  alignSelf: "flex-end",
                  marginBottom: "18px",
                  opacity: partUsageSaving ? 0.6 : 1,
                }}
              >
                {partUsageSaving ? "Adding..." : "➕ Add Part Used"}
              </button>
            </div>

            {partUsageLoading ? (
              <p style={styles.loading}>Loading parts used...</p>
            ) : partUsages.length === 0 ? (
              <div style={styles.emptyCard}>
                No parts have been recorded for this work order yet.
              </div>
            ) : (
              <div style={styles.customerTableContainer}>
                <table style={styles.table}>
                  <thead>
                    <tr>
                      <th style={styles.th}>Part</th>
                      <th style={styles.th}>Part Number</th>
                      <th style={styles.th}>Quantity Used</th>
                      <th style={styles.th}>Used At</th>
                    </tr>
                  </thead>
                  <tbody>
                    {partUsages.map((usage) => (
                      <tr key={usage.id}>
                        <td style={styles.td}>{usage.partName}</td>
                        <td style={styles.td}>{usage.partNumber}</td>
                        <td style={styles.td}>{usage.quantityUsed}</td>
                        <td style={styles.td}>
                          {new Date(usage.usedAt).toLocaleString()}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          <div style={styles.timeLogCard}>
            <div style={styles.partUsageHeader}>
              <div>
                <h3 style={{ margin: 0, color: "#111827" }}>Time Logs</h3>
                <p style={styles.partUsageSubtitle}>
                  Record technician time spent on this work order.
                </p>
              </div>
            </div>

            <div style={styles.timeLogInfo}>
              <div>
                <span style={styles.detailLabel}>Technician</span>
                <strong>
                  {selectedWorkOrder.assignedTechnicianName || "Not assigned"}
                </strong>
              </div>
              <div>
                <span style={styles.detailLabel}>Technician ID</span>
                <strong>
                  {selectedWorkOrder.assignedTechnicianId || "Not assigned"}
                </strong>
              </div>
            </div>

            <div style={styles.timeLogForm}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Start Time *</label>
                <input
                  type="datetime-local"
                  value={timeLogStart}
                  onChange={(e) => setTimeLogStart(e.target.value)}
                  style={styles.input}
                  disabled={timeLogSaving}
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>End Time</label>
                <input
                  type="datetime-local"
                  value={timeLogEnd}
                  onChange={(e) => setTimeLogEnd(e.target.value)}
                  style={styles.input}
                  disabled={timeLogSaving}
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>Notes</label>
                <input
                  type="text"
                  value={timeLogNotes}
                  onChange={(e) => setTimeLogNotes(e.target.value)}
                  style={styles.input}
                  maxLength={500}
                  placeholder="Optional notes"
                  disabled={timeLogSaving}
                />
              </div>

              <div style={styles.timeLogActions}>
                <button
                  onClick={addTimeLog}
                  disabled={timeLogSaving || timeLogLoading || !selectedWorkOrder.assignedTechnicianId}
                  style={{
                    ...styles.createButton,
                    opacity: timeLogSaving || !selectedWorkOrder.assignedTechnicianId ? 0.6 : 1,
                  }}
                >
                  {timeLogSaving ? "Adding..." : "⏱️ Add Time Log"}
                </button>
                <button
                  onClick={resetTimeLogForm}
                  disabled={timeLogSaving}
                  style={styles.cancelButton}
                >
                  Reset
                </button>
              </div>
            </div>

            {!selectedWorkOrder.assignedTechnicianId && (
              <p style={styles.warningMessage}>
                Assign a technician to this work order before adding a time log.
              </p>
            )}

            {timeLogLoading ? (
              <p style={styles.loading}>Loading time logs...</p>
            ) : timeLogs.length === 0 ? (
              <div style={styles.emptyCard}>
                No time logs have been recorded for this work order yet.
              </div>
            ) : (
              <div style={styles.customerTableContainer}>
                <table style={styles.table}>
                  <thead>
                    <tr>
                      <th style={styles.th}>Technician</th>
                      <th style={styles.th}>Start Time</th>
                      <th style={styles.th}>End Time</th>
                      <th style={styles.th}>Notes</th>
                    </tr>
                  </thead>
                  <tbody>
                    {timeLogs.map((log) => (
                      <tr key={log.id}>
                        <td style={styles.td}>{log.technicianName}</td>
                        <td style={styles.td}>
                          {new Date(log.startTime).toLocaleString()}
                        </td>
                        <td style={styles.td}>
                          {log.endTime
                            ? new Date(log.endTime).toLocaleString()
                            : "In progress"}
                        </td>
                        <td style={styles.td}>{log.notes || "—"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </main>
      )}

      {page === "parts" && (
        <main style={styles.main}>
          <button
            onClick={backToDashboard}
            style={styles.backButton}
          >
            ← Back to Dashboard
          </button>

          <div style={styles.pageHeader}>
            <h2 style={styles.welcome}>Parts Management</h2>
          </div>

          {message && (
            <p style={styles.message}>
              {message}
            </p>
          )}

          <div style={styles.formCard}>
            <h3 style={{ marginTop: 0 }}>
              {editingPartId ? "Edit Part" : "Add New Part"}
            </h3>

            <div style={styles.formGroup}>
              <label style={styles.label}>
                Part Name *
              </label>
              <input
                type="text"
                placeholder="Enter part name"
                value={partName}
                onChange={(e) =>
                  setPartName(e.target.value)
                }
                style={styles.input}
                maxLength={150}
              />
            </div>

            <div style={styles.formGroup}>
              <label style={styles.label}>
                Part Number *
              </label>
              <input
                type="text"
                placeholder="Example: PART-001"
                value={partNumber}
                onChange={(e) =>
                  setPartNumber(e.target.value)
                }
                style={styles.input}
                maxLength={50}
              />
            </div>

            <div style={styles.formRow}>
              <div style={styles.formGroupHalf}>
                <label style={styles.label}>
                  Quantity Available *
                </label>
                <input
                  type="number"
                  min="0"
                  step="1"
                  placeholder="Example: 10"
                  value={partQuantity}
                  onChange={(e) =>
                    setPartQuantity(e.target.value)
                  }
                  style={styles.input}
                />
              </div>

              <div style={styles.formGroupHalf}>
                <label style={styles.label}>
                  Unit Price *
                </label>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="Example: 500"
                  value={partUnitPrice}
                  onChange={(e) =>
                    setPartUnitPrice(e.target.value)
                  }
                  style={styles.input}
                />
              </div>
            </div>

            <div style={styles.editActions}>
              <button
                onClick={savePart}
                disabled={partSaving}
                style={{
                  ...styles.createButton,
                  opacity: partSaving ? 0.6 : 1,
                }}
              >
                {partSaving
                  ? "Saving..."
                  : editingPartId
                  ? "Save Changes"
                  : "Add Part"}
              </button>

              {editingPartId && (
                <button
                  onClick={() => {
                    resetPartForm();
                    setMessage("");
                  }}
                  disabled={partSaving}
                  style={styles.cancelButton}
                >
                  Cancel Edit
                </button>
              )}
            </div>
          </div>

          <div style={{ marginTop: "30px" }}>
            <div style={styles.workOrderActions}>
              <h2
                style={{
                  ...styles.sectionTitle,
                  margin: 0,
                  marginRight: "auto",
                }}
              >
                Available Parts
              </h2>

              <button
                onClick={fetchParts}
                style={styles.refreshButton}
              >
                Refresh Parts
              </button>
            </div>

            {partLoading && (
              <p style={styles.loading}>
                Loading parts...
              </p>
            )}

            {!partLoading && parts.length === 0 && (
              <div style={styles.emptyCard}>
                <h3>No parts found</h3>
                <p>
                  There are currently no parts in the system.
                </p>
              </div>
            )}

            {parts.length > 0 && (
              <div style={styles.customerTableContainer}>
                <table style={styles.table}>
                  <thead>
                    <tr>
                      <th style={styles.th}>ID</th>
                      <th style={styles.th}>Name</th>
                      <th style={styles.th}>Part Number</th>
                      <th style={styles.th}>Quantity</th>
                      <th style={styles.th}>Unit Price</th>
                      <th style={styles.th}>Created</th>
                      <th style={styles.th}>Action</th>
                    </tr>
                  </thead>

                  <tbody>
                    {parts.map((part) => (
                      <tr key={part.id}>
                        <td style={styles.td}>
                          {part.id}
                        </td>

                        <td style={styles.td}>
                          <strong>{part.name}</strong>
                        </td>

                        <td style={styles.td}>
                          {part.partNumber}
                        </td>

                        <td style={styles.td}>
                          {part.quantityAvailable}
                        </td>

                        <td style={styles.td}>
                          ₹{Number(part.unitPrice).toFixed(2)}
                        </td>

                        <td style={styles.td}>
                          {new Date(
                            part.createdAt
                          ).toLocaleString()}
                        </td>

                        <td style={styles.td}>
                          <div style={styles.partActions}>
                            <button
                              onClick={() =>
                                startEditPart(part)
                              }
                              style={styles.viewButton}
                            >
                              ✏️ Edit
                            </button>

                            <button
                              onClick={() =>
                                deletePart(part)
                              }
                              style={styles.deleteButton}
                            >
                              🗑️ Delete
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </main>
      )}

      {page === "createWorkOrder" && (
        <main style={styles.main}>

          <button
            onClick={backToWorkOrders}
            style={styles.backButton}
          >
            ← Back to Work Orders
          </button>

          <h2 style={styles.welcome}>
            Create Work Order
          </h2>

          <div style={styles.formCard}>

            <div style={styles.formGroup}>

              <label style={styles.label}>
                Title *
              </label>

              <input
                type="text"
                placeholder="Enter work order title"
                value={createTitle}
                onChange={(e) =>
                  setCreateTitle(e.target.value)
                }
                style={styles.input}
              />

            </div>

            <div style={styles.formGroup}>

              <label style={styles.label}>
                Description
              </label>

              <textarea
                placeholder="Enter work order description"
                value={createDescription}
                onChange={(e) =>
                  setCreateDescription(
                    e.target.value
                  )
                }
                style={styles.textarea}
                rows={5}
              />

            </div>

            <div style={styles.formGroup}>

              <label style={styles.label}>
                Priority *
              </label>

              <select
                value={createPriority}
                onChange={(e) =>
                  setCreatePriority(
                    e.target.value
                  )
                }
                style={styles.input}
              >

                <option value="LOW">
                  LOW
                </option>

                <option value="MEDIUM">
                  MEDIUM
                </option>

                <option value="HIGH">
                  HIGH
                </option>

                <option value="URGENT">
                  URGENT
                </option>

              </select>

            </div>

            <div style={styles.formRow}>

              <div style={styles.formGroupHalf}>

                <label style={styles.label}>
                  Customer ID *
                </label>

                <input
                  type="number"
                  placeholder="Example: 1"
                  value={createCustomerId}
                  onChange={(e) =>
                    setCreateCustomerId(
                      e.target.value
                    )
                  }
                  style={styles.input}
                  min="1"
                />

              </div>

              <div style={styles.formGroupHalf}>

                <label style={styles.label}>
                  Site ID *
                </label>

                <input
                  type="number"
                  placeholder="Example: 1"
                  value={createSiteId}
                  onChange={(e) =>
                    setCreateSiteId(
                      e.target.value
                    )
                  }
                  style={styles.input}
                  min="1"
                />

              </div>

            </div>

            {message && (
              <p style={styles.message}>
                {message}
              </p>
            )}

            <button
              onClick={createWorkOrder}
              disabled={createLoading}
              style={{
                ...styles.createButton,
                opacity: createLoading
                  ? 0.6
                  : 1,
              }}
            >
              {createLoading
                ? "Creating..."
                : "Create Work Order"}
            </button>

          </div>

        </main>
      )}

    </div>
  );
}

const styles: {
  [key: string]: React.CSSProperties;
} = {

  page: {
    minHeight: "100vh",
    background:
      "linear-gradient(135deg, #eef2ff, #f8fafc)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    padding: "30px",
    fontFamily:
      "Arial, Helvetica, sans-serif",
  },

  loginCard: {
    width: "420px",
    background: "#ffffff",
    borderRadius: "16px",
    padding: "35px",
    boxShadow:
      "0 10px 30px rgba(0,0,0,0.12)",
  },

  logoSection: {
    textAlign: "center",
    marginBottom: "30px",
  },

  logo: {
    width: "60px",
    height: "60px",
    margin: "0 auto 15px",
    borderRadius: "14px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: "#2563eb",
    color: "#ffffff",
    fontSize: "32px",
    fontWeight: "bold",
  },

  title: {
    margin: "0",
    fontSize: "24px",
    color: "#111827",
  },

  subtitle: {
    marginTop: "8px",
    color: "#6b7280",
    fontSize: "14px",
  },

  loginTitle: {
    marginBottom: "5px",
    color: "#111827",
  },

  loginSubtitle: {
    color: "#6b7280",
    marginTop: "0",
    marginBottom: "25px",
  },

  form: {
    display: "flex",
    flexDirection: "column",
  },

  label: {
    fontWeight: "600",
    marginBottom: "7px",
    color: "#374151",
    fontSize: "14px",
  },

  input: {
    width: "100%",
    boxSizing: "border-box",
    padding: "12px",
    border:
      "1px solid #d1d5db",
    borderRadius: "8px",
    marginBottom: "18px",
    fontSize: "14px",
    outline: "none",
  },

  textarea: {
    width: "100%",
    boxSizing: "border-box",
    padding: "12px",
    border:
      "1px solid #d1d5db",
    borderRadius: "8px",
    marginBottom: "18px",
    fontSize: "14px",
    fontFamily:
      "Arial, Helvetica, sans-serif",
    resize: "vertical",
  },

  button: {
    padding: "12px",
    border: "none",
    borderRadius: "8px",
    background: "#2563eb",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: "bold",
    cursor: "pointer",
  },

  secondaryButton: {
    marginTop: "10px",
    padding: "11px",
    border: "1px solid #2563eb",
    borderRadius: "8px",
    background: "#ffffff",
    color: "#2563eb",
    fontSize: "15px",
    fontWeight: "bold",
    cursor: "pointer",
  },

  dashboard: {
    minHeight: "100vh",
    background: "#f3f4f6",
    fontFamily:
      "Arial, Helvetica, sans-serif",
  },

  header: {
    background: "#111827",
    color: "#ffffff",
    padding: "18px 35px",
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
  },

  headerTitle: {
    margin: "0",
    fontSize: "22px",
  },

  headerSubtitle: {
    margin: "5px 0 0",
    color: "#d1d5db",
    fontSize: "13px",
  },

  logoutButton: {
    border: "1px solid #6b7280",
    background: "transparent",
    color: "#ffffff",
    padding: "9px 18px",
    borderRadius: "7px",
    cursor: "pointer",
  },

  main: {
    padding: "30px 40px",
    maxWidth: "1400px",
    margin: "0 auto",
  },

  welcome: {
    color: "#111827",
    marginBottom: "25px",
  },

  sectionTitle: {
    marginTop: "35px",
    marginBottom: "20px",
    color: "#111827",
  },

  cards: {
    display: "grid",
    gridTemplateColumns:
      "repeat(auto-fit, minmax(220px, 1fr))",
    gap: "20px",
  },

  card: {
    background: "#ffffff",
    borderRadius: "12px",
    padding: "22px",
    boxShadow:
      "0 3px 10px rgba(0,0,0,0.07)",
  },

  statusCard: {
    background: "#ffffff",
    borderRadius: "12px",
    padding: "22px",
    boxShadow:
      "0 3px 10px rgba(0,0,0,0.07)",
    textAlign: "center",
  },

  number: {
    fontSize: "32px",
    fontWeight: "bold",
    color: "#2563eb",
    margin: "12px 0 0",
  },

  cardLink: {
    color: "#2563eb",
    fontSize: "13px",
    marginTop: "10px",
  },

  backButton: {
    border: "none",
    background: "transparent",
    color: "#2563eb",
    cursor: "pointer",
    padding: "0",
    marginBottom: "15px",
    fontSize: "14px",
    fontWeight: "600",
  },

  searchContainer: {
    display: "flex",
    gap: "10px",
    marginBottom: "20px",
    flexWrap: "wrap",
  },

  searchInput: {
    width: "300px",
    padding: "11px",
    border:
      "1px solid #d1d5db",
    borderRadius: "7px",
    fontSize: "14px",
  },

  searchButton: {
    padding: "10px 18px",
    border: "none",
    borderRadius: "7px",
    background: "#2563eb",
    color: "#ffffff",
    cursor: "pointer",
    fontWeight: "600",
  },

  refreshButton: {
    padding: "10px 18px",
    border: "none",
    borderRadius: "7px",
    background: "#4b5563",
    color: "#ffffff",
    cursor: "pointer",
    fontWeight: "600",
  },

  createButton: {
    padding: "11px 18px",
    border: "none",
    borderRadius: "7px",
    background: "#16a34a",
    color: "#ffffff",
    cursor: "pointer",
    fontWeight: "600",
  },

  workOrderActions: {
    display: "flex",
    gap: "10px",
    marginBottom: "20px",
    flexWrap: "wrap",
  },

  customerTableContainer: {
    background: "#ffffff",
    borderRadius: "12px",
    overflowX: "auto",
    boxShadow:
      "0 3px 10px rgba(0,0,0,0.07)",
  },

  table: {
    width: "100%",
    borderCollapse: "collapse",
    minWidth: "950px",
  },

  th: {
    background: "#f9fafb",
    padding: "14px",
    textAlign: "left",
    fontSize: "13px",
    color: "#374151",
    borderBottom:
      "1px solid #e5e7eb",
  },

  td: {
    padding: "14px",
    borderBottom:
      "1px solid #e5e7eb",
    fontSize: "14px",
    color: "#374151",
  },

  siteButton: {
    padding: "8px 13px",
    border: "none",
    borderRadius: "6px",
    background: "#2563eb",
    color: "#ffffff",
    cursor: "pointer",
  },

  viewButton: {
    padding: "8px 13px",
    border: "none",
    borderRadius: "6px",
    background: "#2563eb",
    color: "#ffffff",
    cursor: "pointer",
  },

  priorityBadge: {
    display: "inline-block",
    padding: "5px 9px",
    borderRadius: "20px",
    background: "#fef3c7",
    color: "#92400e",
    fontSize: "12px",
    fontWeight: "bold",
  },

  statusBadge: {
    display: "inline-block",
    padding: "6px 10px",
    borderRadius: "20px",
    background: "#dbeafe",
    color: "#1e40af",
    fontSize: "12px",
    fontWeight: "bold",
  },

  customerInfo: {
    background: "#ffffff",
    padding: "18px",
    borderRadius: "10px",
    marginBottom: "20px",
    boxShadow:
      "0 3px 10px rgba(0,0,0,0.06)",
  },

  siteCard: {
    background: "#ffffff",
    padding: "22px",
    borderRadius: "12px",
    boxShadow:
      "0 3px 10px rgba(0,0,0,0.07)",
  },

  siteId: {
    color: "#6b7280",
    fontSize: "13px",
    marginTop: "15px",
  },

  emptyCard: {
    background: "#ffffff",
    borderRadius: "12px",
    padding: "40px",
    textAlign: "center",
    color: "#6b7280",
    boxShadow:
      "0 3px 10px rgba(0,0,0,0.06)",
  },

  loading: {
    color: "#2563eb",
    fontWeight: "600",
  },

  message: {
    background: "#eff6ff",
    color: "#1d4ed8",
    padding: "10px 14px",
    borderRadius: "7px",
    marginBottom: "18px",
  },

  footer: {
    textAlign: "center",
    color: "#9ca3af",
    fontSize: "12px",
    marginTop: "25px",
  },

  pageHeader: {
    marginBottom: "5px",
  },

  detailsCard: {
    background: "#ffffff",
    borderRadius: "14px",
    padding: "28px",
    boxShadow:
      "0 4px 15px rgba(0,0,0,0.07)",
  },

  detailsHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
    gap: "20px",
    borderBottom:
      "1px solid #e5e7eb",
    paddingBottom: "20px",
    marginBottom: "25px",
  },

  code: {
    color: "#2563eb",
    fontWeight: "bold",
    margin: "0 0 8px",
    fontSize: "14px",
  },

  detailsGrid: {
    display: "grid",
    gridTemplateColumns:
      "repeat(auto-fit, minmax(220px, 1fr))",
    gap: "20px",
  },

  detailItem: {
    display: "flex",
    flexDirection: "column",
    gap: "7px",
  },

  detailLabel: {
    color: "#6b7280",
    fontSize: "13px",
  },

  descriptionBox: {
    marginTop: "30px",
    padding: "20px",
    background: "#f9fafb",
    borderRadius: "10px",
  },

  detailsActions: {
    display: "flex",
    justifyContent: "flex-end",
    gap: "10px",
    marginBottom: "20px",
  },

  editButton: {
    padding: "10px 18px",
    border: "none",
    borderRadius: "7px",
    background: "#2563eb",
    color: "#ffffff",
    cursor: "pointer",
    fontWeight: "600",
  },

  deleteButton: {
    padding: "10px 18px",
    border: "none",
    borderRadius: "7px",
    background: "#dc2626",
    color: "#ffffff",
    cursor: "pointer",
    fontWeight: "600",
  },

  cancelButton: { padding: "11px 18px", border: "1px solid #d1d5db", borderRadius: "7px", background: "#ffffff", color: "#374151", cursor: "pointer", fontWeight: "600" },

  editForm: { maxWidth: "800px" },

  editInfoGrid: { display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))", gap: "20px", marginBottom: "20px", padding: "18px", background: "#f9fafb", borderRadius: "10px" },

  editActions: { display: "flex", gap: "10px", marginTop: "10px" },

  partActions: {
    display: "flex",
    gap: "8px",
    flexWrap: "wrap",
  },

  timeLogCard: {
    background: "#ffffff",
    borderRadius: "14px",
    padding: "28px",
    marginTop: "24px",
    boxShadow: "0 4px 15px rgba(0,0,0,0.07)",
  },

  timeLogInfo: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
    gap: "20px",
    padding: "16px",
    marginBottom: "20px",
    background: "#f9fafb",
    borderRadius: "10px",
  },

  timeLogForm: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(210px, 1fr))",
    gap: "16px",
    alignItems: "end",
    marginBottom: "18px",
  },

  timeLogActions: {
    display: "flex",
    gap: "10px",
    alignItems: "center",
    flexWrap: "wrap",
  },

  warningMessage: {
    background: "#fff7ed",
    color: "#c2410c",
    padding: "10px 14px",
    borderRadius: "7px",
    marginBottom: "18px",
  },

  partUsageCard: {
    background: "#ffffff",
    borderRadius: "14px",
    padding: "28px",
    marginTop: "24px",
    boxShadow: "0 4px 15px rgba(0,0,0,0.07)",
  },

  partUsageHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
    marginBottom: "20px",
  },

  partUsageSubtitle: {
    margin: "7px 0 0",
    color: "#6b7280",
    fontSize: "13px",
  },

  partUsageForm: {
    display: "grid",
    gridTemplateColumns: "minmax(240px, 1fr) 180px auto",
    gap: "16px",
    alignItems: "end",
    marginBottom: "10px",
  },

  formCard: {
    background: "#ffffff",
    borderRadius: "14px",
    padding: "30px",
    maxWidth: "800px",
    boxShadow:
      "0 4px 15px rgba(0,0,0,0.07)",
  },

  formGroup: {
    display: "flex",
    flexDirection: "column",
  },

  formRow: {
    display: "grid",
    gridTemplateColumns:
      "1fr 1fr",
    gap: "20px",
  },

  formGroupHalf: {
    display: "flex",
    flexDirection: "column",
  },

};

export default App;
